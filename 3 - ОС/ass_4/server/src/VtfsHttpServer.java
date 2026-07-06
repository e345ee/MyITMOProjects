import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * VTFS HTTP server implementation in Java + PostgreSQL.
 *
 * Protocol compatibility:
 *  - every /api/* response returns HTTP 200 and binary body
 *  - first 8 bytes: little-endian int64 (0 on success, errno on error)
 *  - remaining bytes: method-specific payload
 */
public class VtfsHttpServer {
    private static final int VTFS_DIR = 0;
    private static final int VTFS_REG = 1;

    // Linux errno values used by the kernel module
    private static final int EPERM = 1;
    private static final int ENOENT = 2;
    private static final int EIO = 5;
    private static final int EACCES = 13;
    private static final int EEXIST = 17;
    private static final int ENOTDIR = 20;
    private static final int EISDIR = 21;
    private static final int EINVAL = 22;
    private static final int ENOSPC = 28;
    private static final int ENOTEMPTY = 39;

    public static void main(String[] args) throws Exception {
        DbConfig db = DbConfig.fromEnv();
        ServerConfig cfg = ServerConfig.fromEnv();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found. Add postgresql-*.jar to classpath.");
            throw e;
        }

        try (Connection conn = db.open()) {
            ensureSchema(conn);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(cfg.host, cfg.port), 0);
        server.createContext("/api/", new ApiHandler(db));
        server.setExecutor(Executors.newFixedThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors())));
        server.start();

        System.out.println("VTFS Java server listening on http://" + cfg.host + ":" + cfg.port);
        System.out.println("DB: " + db.jdbcUrl);
    }

    private static final class ApiHandler implements HttpHandler {
        private final DbConfig dbConfig;

        ApiHandler(DbConfig dbConfig) {
            this.dbConfig = dbConfig;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()) && !"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    sendPlain(exchange, 405, "Method Not Allowed");
                    return;
                }

                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                if (path == null || !path.startsWith("/api/")) {
                    sendPlain(exchange, 404, "Not Found");
                    return;
                }

                String method = path.substring("/api/".length());
                Map<String, String> params = parseQuery(uri.getRawQuery());
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    byte[] body = readAll(exchange.getRequestBody());
                    // only /api/write uses POST body, but merging is harmless
                    params.putAll(parseFormEncoded(body));
                }

                String token = params.get("token");
                if (token == null && !"write".equals(method)) {
                    // keep protocol style: binary payload with errno, HTTP 200
                    sendApi(exchange, EINVAL, null);
                    return;
                }
                if (token == null) {
                    // POST /api/write still needs token in query
                    sendApi(exchange, EINVAL, null);
                    return;
                }

                ApiResponse result;
                switch (method) {
                    case "init" -> result = handleInit(token);
                    case "getattr" -> result = handleGetattr(token, params);
                    case "lookup" -> result = handleLookup(token, params);
                    case "readdir" -> result = handleReaddir(token, params);
                    case "create" -> result = handleCreate(token, params);
                    case "unlink" -> result = handleUnlink(token, params);
                    case "link" -> result = handleLink(token, params);
                    case "read" -> result = handleRead(token, params);
                    case "write" -> result = handleWrite(token, params);
                    case "truncate" -> result = handleTruncate(token, params);
                    case "chmod" -> result = handleChmod(token, params);
                    default -> {
                        sendPlain(exchange, 404, "Unknown endpoint");
                        return;
                    }
                }

                sendApi(exchange, result.errno, result.payload);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                sendApi(exchange, EIO, null);
            } finally {
                exchange.close();
            }
        }

        private ApiResponse handleInit(String token) {
            try (Connection conn = dbConfig.open()) {
                conn.setAutoCommit(false);
                try {
                    Long rootId = findTokenRoot(conn, token);
                    if (rootId == null) {
                        long newRoot = createInode(conn, VTFS_DIR, 0_777, 0L, 2, null);
                        try (PreparedStatement ps = conn.prepareStatement(
                                "INSERT INTO tokens(token, root_id) VALUES (?, ?)") ) {
                            ps.setString(1, token);
                            ps.setLong(2, newRoot);
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            if (!isUniqueViolation(ex)) throw ex;
                            // race: another request inserted token; cleanup orphan root and reuse existing
                            try (PreparedStatement del = conn.prepareStatement("DELETE FROM inodes WHERE id = ?")) {
                                del.setLong(1, newRoot);
                                del.executeUpdate();
                            }
                        }
                        rootId = findTokenRoot(conn, token);
                    }
                    conn.commit();
                    return ApiResponse.success(leU64(rootId));
                } catch (Exception e) {
                    rollbackQuietly(conn);
                    if (e instanceof SQLException) e.printStackTrace(System.err);
                    return ApiResponse.error(EIO);
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleGetattr(String token, Map<String, String> params) {
            Long id = parseLong(params.get("id"));
            if (id == null) return ApiResponse.error(EINVAL);

            try (Connection conn = dbConfig.open()) {
                long root = requireRoot(conn, token);
                if (root < 0) return ApiResponse.error(ENOENT);

                InodeRow inode = getInodeInScope(conn, root, id, false);
                if (inode == null) return ApiResponse.error(ENOENT);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write((byte) inode.type);
                out.write(leU32(inode.mode));
                out.write(leU64(inode.size));
                out.write(leU32(inode.nlink));
                return ApiResponse.success(out.toByteArray());
            } catch (Exception e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleLookup(String token, Map<String, String> params) {
            Long parent = parseLong(params.get("parent"));
            String name = params.get("name");
            if (parent == null || name == null) return ApiResponse.error(EINVAL);

            try (Connection conn = dbConfig.open()) {
                long root = requireRoot(conn, token);
                if (root < 0) return ApiResponse.error(ENOENT);

                InodeRow parentInode = getInodeInScope(conn, root, parent, false);
                if (parentInode == null || parentInode.type != VTFS_DIR) return ApiResponse.error(ENOTDIR);

                Long inodeId = lookupDentryInScope(conn, root, parent, name);
                if (inodeId == null) return ApiResponse.error(ENOENT);

                return ApiResponse.success(leU64(inodeId));
            } catch (Exception e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleReaddir(String token, Map<String, String> params) {
            Long dirId = parseLong(params.get("dir"));
            Long cursorL = parseLong(params.get("cursor"));
            Long limitL = parseLong(params.get("limit"));
            if (dirId == null || cursorL == null || limitL == null) return ApiResponse.error(EINVAL);

            long cursor = Math.max(0L, cursorL);
            long limitLong = Math.max(0L, limitL);
            int limit = (int) Math.min(Integer.MAX_VALUE, limitLong);

            try (Connection conn = dbConfig.open()) {
                long root = requireRoot(conn, token);
                if (root < 0) return ApiResponse.error(ENOENT);

                InodeRow dir = getInodeInScope(conn, root, dirId, false);
                if (dir == null || dir.type != VTFS_DIR) return ApiResponse.error(ENOTDIR);

                List<DirEntryRow> rows = readdirInScope(conn, root, dirId, cursor, limit);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(leU32(rows.size()));
                for (DirEntryRow r : rows) {
                    byte[] nameBytes = r.name.getBytes(StandardCharsets.UTF_8);
                    int nlen = Math.min(nameBytes.length, 0xFFFF);
                    out.write(leU64(r.inodeId));
                    out.write((byte) r.type);
                    out.write(leU16(nlen));
                    out.write(nameBytes, 0, nlen);
                }
                return ApiResponse.success(out.toByteArray());
            } catch (Exception e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleCreate(String token, Map<String, String> params) {
            Long parent = parseLong(params.get("parent"));
            String name = params.get("name");
            String type = params.get("type");
            Long modeL = parseLong(params.get("mode"));
            if (parent == null || name == null || type == null || modeL == null) return ApiResponse.error(EINVAL);

            int inodeType;
            if ("dir".equals(type)) inodeType = VTFS_DIR;
            else if ("reg".equals(type)) inodeType = VTFS_REG;
            else return ApiResponse.error(EACCES);

            int mode = (int) (modeL & 0x1FFL); // 0777

            try (Connection conn = dbConfig.open()) {
                conn.setAutoCommit(false);
                try {
                    long root = requireRoot(conn, token);
                    if (root < 0) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }

                    InodeRow parentInode = getInodeInScope(conn, root, parent, true);
                    if (parentInode == null || parentInode.type != VTFS_DIR) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOTDIR);
                    }

                    if (dentryExists(conn, parent, name)) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(EEXIST);
                    }

                    int initialNlink = (inodeType == VTFS_DIR) ? 2 : 1;
                    long newId = createInode(conn, inodeType, mode, 0L, initialNlink, null);
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO dentries(parent_id, name, inode_id) VALUES (?, ?, ?)") ) {
                        ps.setLong(1, parent);
                        ps.setString(2, name);
                        ps.setLong(3, newId);
                        ps.executeUpdate();
                    }

                    if (inodeType == VTFS_DIR) {
                        try (PreparedStatement ps = conn.prepareStatement(
                                "UPDATE inodes SET nlink = COALESCE(nlink, 2) + 1 WHERE id = ?")) {
                            ps.setLong(1, parent);
                            ps.executeUpdate();
                        }
                    }

                    conn.commit();
                    return ApiResponse.success(leU64(newId));
                } catch (SQLException e) {
                    rollbackQuietly(conn);
                    if (isUniqueViolation(e)) return ApiResponse.error(EEXIST);
                    e.printStackTrace(System.err);
                    return ApiResponse.error(EIO);
                } catch (Exception e) {
                    rollbackQuietly(conn);
                    e.printStackTrace(System.err);
                    return ApiResponse.error(EIO);
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleUnlink(String token, Map<String, String> params) {
            Long parent = parseLong(params.get("parent"));
            String name = params.get("name");
            if (parent == null || name == null) return ApiResponse.error(EINVAL);

            try (Connection conn = dbConfig.open()) {
                conn.setAutoCommit(false);
                try {
                    long root = requireRoot(conn, token);
                    if (root < 0) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }

                    InodeRow parentInode = getInodeInScope(conn, root, parent, true);
                    if (parentInode == null || parentInode.type != VTFS_DIR) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOTDIR);
                    }

                    DentryTarget target = getDentryTargetInScope(conn, root, parent, name, true);
                    if (target == null) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }

                    if (target.type == VTFS_DIR) {
                        if (directoryHasChildren(conn, target.inodeId)) {
                            rollbackQuietly(conn);
                            return ApiResponse.error(ENOTEMPTY);
                        }

                        try (PreparedStatement ps = conn.prepareStatement(
                                "UPDATE inodes SET nlink = GREATEST(2, COALESCE(nlink, 2) - 1) WHERE id = ?")) {
                            ps.setLong(1, parent);
                            ps.executeUpdate();
                        }

                        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dentries WHERE id = ?")) {
                            ps.setLong(1, target.dentryId);
                            ps.executeUpdate();
                        }

                        // Directory dentries do not use hard-link style nlink accounting here:
                        // remove the now-empty directory inode directly.
                        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM inodes WHERE id = ?")) {
                            ps.setLong(1, target.inodeId);
                            ps.executeUpdate();
                        }

                        conn.commit();
                        return ApiResponse.success(null);
                    }

                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dentries WHERE id = ?")) {
                        ps.setLong(1, target.dentryId);
                        ps.executeUpdate();
                    }

                    int newNlink = target.nlink - 1;
                    if (newNlink <= 0) {
                        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM inodes WHERE id = ?")) {
                            ps.setLong(1, target.inodeId);
                            ps.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement ps = conn.prepareStatement("UPDATE inodes SET nlink = ? WHERE id = ?")) {
                            ps.setInt(1, newNlink);
                            ps.setLong(2, target.inodeId);
                            ps.executeUpdate();
                        }
                    }

                    conn.commit();
                    return ApiResponse.success(null);
                } catch (Exception e) {
                    rollbackQuietly(conn);
                    if (e instanceof SQLException) ((SQLException)e).printStackTrace(System.err); else e.printStackTrace(System.err);
                    return ApiResponse.error(EIO);
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleLink(String token, Map<String, String> params) {
            Long oldId = parseLong(params.get("old"));
            Long parent = parseLong(params.get("parent"));
            String name = params.get("name");
            if (oldId == null || parent == null || name == null) return ApiResponse.error(EINVAL);

            try (Connection conn = dbConfig.open()) {
                conn.setAutoCommit(false);
                try {
                    long root = requireRoot(conn, token);
                    if (root < 0) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }

                    InodeRow parentInode = getInodeInScope(conn, root, parent, true);
                    if (parentInode == null || parentInode.type != VTFS_DIR) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOTDIR);
                    }

                    InodeRow inode = getInodeInScope(conn, root, oldId, true);
                    if (inode == null) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }
                    if (inode.type == VTFS_DIR) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(EPERM);
                    }

                    if (dentryExists(conn, parent, name)) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(EEXIST);
                    }

                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO dentries(parent_id, name, inode_id) VALUES (?, ?, ?)") ) {
                        ps.setLong(1, parent);
                        ps.setString(2, name);
                        ps.setLong(3, oldId);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE inodes SET nlink = COALESCE(nlink, 0) + 1 WHERE id = ?")) {
                        ps.setLong(1, oldId);
                        ps.executeUpdate();
                    }

                    conn.commit();
                    return ApiResponse.success(null);
                } catch (SQLException e) {
                    rollbackQuietly(conn);
                    if (isUniqueViolation(e)) return ApiResponse.error(EEXIST);
                    e.printStackTrace(System.err);
                    return ApiResponse.error(EIO);
                } catch (Exception e) {
                    rollbackQuietly(conn);
                    e.printStackTrace(System.err);
                    return ApiResponse.error(EIO);
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleRead(String token, Map<String, String> params) {
            Long id = parseLong(params.get("id"));
            Long offL = parseLong(params.get("off"));
            Long lenL = parseLong(params.get("len"));
            if (id == null || offL == null || lenL == null) return ApiResponse.error(EINVAL);
            if (offL < 0 || lenL < 0) return ApiResponse.error(EINVAL);
            if (lenL > Integer.MAX_VALUE || offL > Integer.MAX_VALUE) return ApiResponse.error(EINVAL);
            int off = offL.intValue();
            int len = lenL.intValue();

            try (Connection conn = dbConfig.open()) {
                long root = requireRoot(conn, token);
                if (root < 0) return ApiResponse.error(ENOENT);

                InodeRow inode = getInodeInScope(conn, root, id, false);
                if (inode == null) return ApiResponse.error(ENOENT);
                if (inode.type != VTFS_REG) return ApiResponse.error(EISDIR);

                byte[] data = inode.data;
                byte[] slice;
                if (data == null || off >= data.length) {
                    slice = new byte[0];
                } else {
                    int end = Math.min(data.length, off + len);
                    int n = Math.max(0, end - off);
                    slice = new byte[n];
                    System.arraycopy(data, off, slice, 0, n);
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(leU32(slice.length));
                out.write(slice);
                return ApiResponse.success(out.toByteArray());
            } catch (Exception e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleWrite(String token, Map<String, String> params) {
            Long id = parseLong(params.get("id"));
            Long offL = parseLong(params.get("off"));
            Long lenL = parseLong(params.get("len"));
            String dataB64 = params.get("data");
            if (id == null || offL == null || lenL == null || dataB64 == null) return ApiResponse.error(EINVAL);
            if (offL < 0 || lenL < 0) return ApiResponse.error(EINVAL);
            if (offL > Integer.MAX_VALUE || lenL > Integer.MAX_VALUE) return ApiResponse.error(EINVAL);

            byte[] decoded;
            try {
                decoded = decodeBase64Url(dataB64);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(EINVAL);
            }
            if (decoded.length != lenL.intValue()) return ApiResponse.error(EINVAL);

            long endLong = offL + lenL;
            if (endLong < 0 || endLong > Integer.MAX_VALUE) return ApiResponse.error(ENOSPC);
            int off = offL.intValue();
            int end = (int) endLong;

            try (Connection conn = dbConfig.open()) {
                conn.setAutoCommit(false);
                try {
                    long root = requireRoot(conn, token);
                    if (root < 0) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }

                    InodeRow inode = getInodeInScope(conn, root, id, true);
                    if (inode == null) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }
                    if (inode.type != VTFS_REG) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(EISDIR);
                    }

                    byte[] current = inode.data;
                    byte[] out;
                    if (current == null) {
                        out = new byte[end];
                        System.arraycopy(decoded, 0, out, off, decoded.length);
                    } else {
                        int newLen = Math.max(current.length, end);
                        out = new byte[newLen];
                        System.arraycopy(current, 0, out, 0, current.length);
                        System.arraycopy(decoded, 0, out, off, decoded.length);
                    }

                    long newSize = Math.max(inode.size, endLong);
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE inodes SET data = ?, size = ? WHERE id = ?")) {
                        ps.setBytes(1, out);
                        ps.setLong(2, newSize);
                        ps.setLong(3, id);
                        ps.executeUpdate();
                    }

                    conn.commit();
                    return ApiResponse.success(null);
                } catch (Exception e) {
                    rollbackQuietly(conn);
                    if (e instanceof SQLException) ((SQLException)e).printStackTrace(System.err); else e.printStackTrace(System.err);
                    return ApiResponse.error(EIO);
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleChmod(String token, Map<String, String> params) {
            Long id = parseLong(params.get("id"));
            Long modeL = parseLong(params.get("mode"));
            if (id == null || modeL == null) return ApiResponse.error(EINVAL);
            if (modeL < 0) return ApiResponse.error(EINVAL);
            int mode = (int) (modeL & 0x1FFL);

            try (Connection conn = dbConfig.open()) {
                conn.setAutoCommit(false);
                try {
                    long root = requireRoot(conn, token);
                    if (root < 0) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }

                    InodeRow inode = getInodeInScope(conn, root, id, true);
                    if (inode == null) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }

                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE inodes SET mode = ? WHERE id = ?")) {
                        ps.setInt(1, mode);
                        ps.setLong(2, id);
                        ps.executeUpdate();
                    }

                    conn.commit();
                    return ApiResponse.success(null);
                } catch (Exception e) {
                    rollbackQuietly(conn);
                    if (e instanceof SQLException) ((SQLException)e).printStackTrace(System.err); else e.printStackTrace(System.err);
                    return ApiResponse.error(EIO);
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        private ApiResponse handleTruncate(String token, Map<String, String> params) {
            Long id = parseLong(params.get("id"));
            Long szL = parseLong(params.get("sz"));
            if (id == null || szL == null) return ApiResponse.error(EINVAL);
            if (szL < 0 || szL > Integer.MAX_VALUE) return ApiResponse.error(EINVAL);
            int newSize = szL.intValue();

            try (Connection conn = dbConfig.open()) {
                conn.setAutoCommit(false);
                try {
                    long root = requireRoot(conn, token);
                    if (root < 0) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }

                    InodeRow inode = getInodeInScope(conn, root, id, true);
                    if (inode == null) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(ENOENT);
                    }
                    if (inode.type != VTFS_REG) {
                        rollbackQuietly(conn);
                        return ApiResponse.error(EISDIR);
                    }

                    byte[] data;
                    if (newSize == 0) {
                        data = null;
                    } else if (inode.data == null) {
                        data = new byte[newSize];
                    } else if (inode.data.length == newSize) {
                        data = inode.data;
                    } else {
                        data = new byte[newSize];
                        System.arraycopy(inode.data, 0, data, 0, Math.min(inode.data.length, newSize));
                    }

                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE inodes SET data = ?, size = ? WHERE id = ?")) {
                        if (data == null) {
                            ps.setNull(1, Types.BINARY);
                        } else {
                            ps.setBytes(1, data);
                        }
                        ps.setLong(2, newSize);
                        ps.setLong(3, id);
                        ps.executeUpdate();
                    }

                    conn.commit();
                    return ApiResponse.success(null);
                } catch (Exception e) {
                    rollbackQuietly(conn);
                    if (e instanceof SQLException) ((SQLException)e).printStackTrace(System.err); else e.printStackTrace(System.err);
                    return ApiResponse.error(EIO);
                }
            } catch (SQLException e) {
                e.printStackTrace(System.err);
                return ApiResponse.error(EIO);
            }
        }

        // ---------- DB helpers ----------

        private long requireRoot(Connection conn, String token) throws SQLException {
            Long root = findTokenRoot(conn, token);
            return (root == null) ? -1L : root;
        }

        private Long findTokenRoot(Connection conn, String token) throws SQLException {
            try (PreparedStatement ps = conn.prepareStatement("SELECT root_id FROM tokens WHERE token = ?")) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                    return null;
                }
            }
        }

        private long createInode(Connection conn, int type, int mode, long size, int nlink, byte[] data) throws SQLException {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO inodes(type, mode, size, nlink, data) VALUES (?, ?, ?, ?, ?) RETURNING id")) {
                ps.setInt(1, type);
                ps.setInt(2, mode);
                ps.setLong(3, size);
                ps.setInt(4, nlink);
                if (data == null) ps.setNull(5, Types.BINARY); else ps.setBytes(5, data);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("No id returned", "99999");
                    return rs.getLong(1);
                }
            }
        }

        private InodeRow getInodeInScope(Connection conn, long rootId, long inodeId, boolean forUpdate) throws SQLException {
            String sql =
                    "WITH RECURSIVE scope(id) AS (" +
                    "  SELECT ?::bigint" +
                    "  UNION" +
                    "  SELECT d.inode_id FROM dentries d JOIN scope s ON d.parent_id = s.id" +
                    ") " +
                    "SELECT i.id, i.type, i.mode, i.size, i.nlink, i.data FROM inodes i " +
                    "JOIN scope s ON s.id = i.id WHERE i.id = ?" +
                    (forUpdate ? " FOR UPDATE" : "");
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, rootId);
                ps.setLong(2, inodeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    return new InodeRow(
                            rs.getLong("id"),
                            rs.getInt("type"),
                            rs.getInt("mode"),
                            rs.getLong("size"),
                            rs.getInt("nlink"),
                            rs.getBytes("data")
                    );
                }
            }
        }

        private Long lookupDentryInScope(Connection conn, long rootId, long parentId, String name) throws SQLException {
            String sql =
                    "WITH RECURSIVE scope(id) AS (" +
                    "  SELECT ?::bigint" +
                    "  UNION" +
                    "  SELECT d.inode_id FROM dentries d JOIN scope s ON d.parent_id = s.id" +
                    ") " +
                    "SELECT d.inode_id FROM dentries d JOIN scope s ON s.id = d.inode_id " +
                    "WHERE d.parent_id = ? AND d.name = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, rootId);
                ps.setLong(2, parentId);
                ps.setString(3, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                    return null;
                }
            }
        }

        private List<DirEntryRow> readdirInScope(Connection conn, long rootId, long dirId, long cursor, int limit) throws SQLException {
            String sql =
                    "WITH RECURSIVE scope(id) AS (" +
                    "  SELECT ?::bigint" +
                    "  UNION" +
                    "  SELECT d.inode_id FROM dentries d JOIN scope s ON d.parent_id = s.id" +
                    ") " +
                    "SELECT d.inode_id, i.type, d.name " +
                    "FROM dentries d " +
                    "JOIN inodes i ON i.id = d.inode_id " +
                    "JOIN scope s ON s.id = d.inode_id " +
                    "WHERE d.parent_id = ? " +
                    "ORDER BY d.id OFFSET ? LIMIT ?";
            List<DirEntryRow> rows = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, rootId);
                ps.setLong(2, dirId);
                ps.setLong(3, cursor);
                ps.setInt(4, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new DirEntryRow(rs.getLong(1), rs.getInt(2), rs.getString(3)));
                    }
                }
            }
            return rows;
        }

        private boolean dentryExists(Connection conn, long parentId, String name) throws SQLException {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM dentries WHERE parent_id = ? AND name = ? LIMIT 1")) {
                ps.setLong(1, parentId);
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }

        private DentryTarget getDentryTargetInScope(Connection conn, long rootId, long parentId, String name, boolean forUpdate) throws SQLException {
            String sql =
                    "WITH RECURSIVE scope(id) AS (" +
                    "  SELECT ?::bigint" +
                    "  UNION" +
                    "  SELECT d2.inode_id FROM dentries d2 JOIN scope s ON d2.parent_id = s.id" +
                    ") " +
                    "SELECT d.id AS dentry_id, d.inode_id, i.type, i.nlink " +
                    "FROM dentries d " +
                    "JOIN inodes i ON i.id = d.inode_id " +
                    "JOIN scope s ON s.id = d.inode_id " +
                    "WHERE d.parent_id = ? AND d.name = ? " +
                    (forUpdate ? "FOR UPDATE OF d, i" : "") +
                    " LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, rootId);
                ps.setLong(2, parentId);
                ps.setString(3, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    return new DentryTarget(rs.getLong("dentry_id"), rs.getLong("inode_id"), rs.getInt("type"), rs.getInt("nlink"));
                }
            }
        }

        private boolean directoryHasChildren(Connection conn, long inodeId) throws SQLException {
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM dentries WHERE parent_id = ? LIMIT 1")) {
                ps.setLong(1, inodeId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    private static byte[] decodeBase64Url(String data) {
        String s = data;
        int rem = s.length() % 4;
        if (rem != 0) s = s + "=".repeat(4 - rem);
        return Base64.getUrlDecoder().decode(s);
    }

    private static void ensureSchema(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS inodes (
                        id BIGSERIAL PRIMARY KEY,
                        type SMALLINT NOT NULL,
                        mode INTEGER NOT NULL DEFAULT 511,
                        size BIGINT NOT NULL DEFAULT 0,
                        nlink INTEGER NOT NULL DEFAULT 1,
                        data BYTEA NULL,
                        created_at TIMESTAMPTZ DEFAULT NOW(),
                        updated_at TIMESTAMPTZ DEFAULT NOW()
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS dentries (
                        id SERIAL PRIMARY KEY,
                        parent_id BIGINT NOT NULL REFERENCES inodes(id) ON DELETE CASCADE,
                        name VARCHAR(255) NOT NULL,
                        inode_id BIGINT NOT NULL REFERENCES inodes(id) ON DELETE CASCADE,
                        UNIQUE(parent_id, name)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS tokens (
                        token VARCHAR(255) PRIMARY KEY,
                        root_id BIGINT NOT NULL REFERENCES inodes(id) ON DELETE CASCADE,
                        created_at TIMESTAMPTZ DEFAULT NOW()
                    )
                    """);
            st.execute("CREATE INDEX IF NOT EXISTS idx_dentries_parent_id ON dentries(parent_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_dentries_inode_id ON dentries(inode_id)");
            st.execute("CREATE OR REPLACE FUNCTION vtfs_touch_updated_at() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = NOW(); RETURN NEW; END; $$ LANGUAGE plpgsql");
            st.execute("DROP TRIGGER IF EXISTS trg_vtfs_inodes_updated_at ON inodes");
            st.execute("CREATE TRIGGER trg_vtfs_inodes_updated_at BEFORE UPDATE ON inodes FOR EACH ROW EXECUTE FUNCTION vtfs_touch_updated_at()");
        }
        conn.commit();
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        if (rawQuery == null || rawQuery.isEmpty()) return new HashMap<>();
        return parseForm(rawQuery);
    }

    private static Map<String, String> parseFormEncoded(byte[] body) {
        if (body == null || body.length == 0) return new HashMap<>();
        return parseForm(new String(body, StandardCharsets.UTF_8));
    }

    private static Map<String, String> parseForm(String encoded) {
        Map<String, String> out = new HashMap<>();
        if (encoded == null || encoded.isEmpty()) return out;
        String[] parts = encoded.split("&");
        for (String part : parts) {
            if (part.isEmpty()) continue;
            int idx = part.indexOf('=');
            String k = idx >= 0 ? part.substring(0, idx) : part;
            String v = idx >= 0 ? part.substring(idx + 1) : "";
            try {
                k = URLDecoder.decode(k, StandardCharsets.UTF_8);
                v = URLDecoder.decode(v, StandardCharsets.UTF_8);
                out.put(k, v);
            } catch (IllegalArgumentException ignored) {
                // keep best-effort behavior; invalid encoding handled later via missing/invalid params
            }
        }
        return out;
    }

    private static byte[] readAll(InputStream in) throws IOException {
        try (in; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            return out.toByteArray();
        }
    }

    private static void sendApi(HttpExchange exchange, int errno, byte[] payload) throws IOException {
        if (payload == null) payload = new byte[0];
        ByteBuffer bb = ByteBuffer.allocate(8 + payload.length).order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(errno);
        bb.put(payload);
        byte[] body = bb.array();

        Headers h = exchange.getResponseHeaders();
        h.set("Content-Type", "application/octet-stream");
        h.set("Connection", "close");
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
    }

    private static void sendPlain(HttpExchange exchange, int status, String text) throws IOException {
        byte[] body = text.getBytes(StandardCharsets.UTF_8);
        Headers h = exchange.getResponseHeaders();
        h.set("Content-Type", "text/plain; charset=utf-8");
        h.set("Connection", "close");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
    }

    private static byte[] leU16(int value) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) (value & 0xFFFF)).array();
    }

    private static byte[] leU32(long value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) (value & 0xFFFF_FFFFL)).array();
    }

    private static byte[] leU64(long value) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
    }

    private static Long parseLong(String s) {
        if (s == null) return null;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
        }
    }

    private static boolean isUniqueViolation(SQLException e) {
        return "23505".equals(e.getSQLState());
    }

    private static final class ApiResponse {
        final int errno;
        final byte[] payload;

        private ApiResponse(int errno, byte[] payload) {
            this.errno = errno;
            this.payload = payload;
        }

        static ApiResponse success(byte[] payload) {
            return new ApiResponse(0, payload);
        }

        static ApiResponse error(int errno) {
            return new ApiResponse(errno, null);
        }
    }

    private static final class InodeRow {
        final long id;
        final int type;
        final int mode;
        final long size;
        final int nlink;
        final byte[] data;

        InodeRow(long id, int type, int mode, long size, int nlink, byte[] data) {
            this.id = id;
            this.type = type;
            this.mode = mode;
            this.size = size;
            this.nlink = nlink;
            this.data = data;
        }
    }

    private static final class DirEntryRow {
        final long inodeId;
        final int type;
        final String name;

        DirEntryRow(long inodeId, int type, String name) {
            this.inodeId = inodeId;
            this.type = type;
            this.name = name;
        }
    }

    private static final class DentryTarget {
        final long dentryId;
        final long inodeId;
        final int type;
        final int nlink;

        DentryTarget(long dentryId, long inodeId, int type, int nlink) {
            this.dentryId = dentryId;
            this.inodeId = inodeId;
            this.type = type;
            this.nlink = nlink;
        }
    }

    private static final class ServerConfig {
        final String host;
        final int port;

        ServerConfig(String host, int port) {
            this.host = host;
            this.port = port;
        }

        static ServerConfig fromEnv() {
            String host = env("VTFS_HOST", "0.0.0.0");
            int port = parseInt(env("VTFS_PORT", "8080"), 8080);
            return new ServerConfig(host, port);
        }
    }

    private static final class DbConfig {
        final String jdbcUrl;
        final String user;
        final String password;

        DbConfig(String jdbcUrl, String user, String password) {
            this.jdbcUrl = jdbcUrl;
            this.user = user;
            this.password = password;
        }

        static DbConfig fromEnv() {
            String raw = env("VTFS_DATABASE_URL", "postgresql://vtfs:vtfs123@localhost:5432/vtfs_db");
            String user = env("VTFS_DB_USER", null);
            String pass = env("VTFS_DB_PASSWORD", null);

            if (raw.startsWith("postgresql://")) {
                URI uri = URI.create(raw);
                String host = (uri.getHost() == null || uri.getHost().isEmpty()) ? "localhost" : uri.getHost();
                int port = (uri.getPort() <= 0) ? 5432 : uri.getPort();
                String path = (uri.getPath() == null || uri.getPath().isEmpty()) ? "/vtfs_db" : uri.getPath();
                String jdbc = "jdbc:postgresql://" + host + ":" + port + path;

                if (uri.getUserInfo() != null && !uri.getUserInfo().isEmpty()) {
                    String[] parts = uri.getUserInfo().split(":", 2);
                    if (user == null) user = parts[0];
                    if (pass == null) pass = parts.length > 1 ? parts[1] : "";
                }
                if (user == null) user = "vtfs";
                if (pass == null) pass = "vtfs123";
                return new DbConfig(jdbc, user, pass);
            }

            if (raw.startsWith("jdbc:postgresql://")) {
                if (user == null) user = "vtfs";
                if (pass == null) pass = "vtfs123";
                return new DbConfig(raw, user, pass);
            }

            // best effort fallback
            if (user == null) user = "vtfs";
            if (pass == null) pass = "vtfs123";
            return new DbConfig("jdbc:postgresql://localhost:5432/vtfs_db", user, pass);
        }

        Connection open() throws SQLException {
            Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
            conn.setAutoCommit(false);
            return conn;
        }
    }

    private static String env(String key, String def) {
        String v = System.getenv(key);
        if (v == null || v.isEmpty()) return def;
        return v;
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
}
