package collectionStorageManager;

import client.ClientHandler;
import client.PasswordHandler;
import models.handlers.ProductHandler;
import products.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class PostgresSQLManager implements DatabaseManager {
    private static final Logger logger = LogManager.getLogger("PostgresSQLManager");

    // Метод для получения соединения с базой данных и загрузки конфигурации
    private Connection getConnection() throws SQLException, IOException {
        Properties info = new Properties();
        try (InputStream is = this.getClass().getResourceAsStream("/db.cfg")) {
            info.load(is);
        }
        return DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", info);
    }
//TtSXRLK86yz5ENVZ
    @Override
    public ArrayList<Product> getCollectionFromDatabase() {
        ArrayList<Product> data = new ArrayList<>();
        String query = "SELECT p.*, co.x, co.y, " +
                "o.id AS manufacturer_id, o.name AS manufacturer_name, o.employees_count, o.type, " +
                "a.street AS address_street, a.zip_code AS address_zip_code " +
                "FROM Product p " +
                "JOIN Organization o ON p.manufacturer_id = o.id " +
                "JOIN Coordinates co ON p.coordinates_id = co.id " +
                "LEFT JOIN Address a ON o.official_address_id = a.id";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Product product = extractProductFromResultSet(resultSet);
                data.add(product);
            }
        } catch (SQLException | IOException e) {
            logger.error("Ошибка при получении данных из базы данных: ", e);
        }
        return data;
    }

    @Override
    public void writeCollectionToDatabase(ClientHandler clientHandler) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            Set<Integer> existingProductIds = getExistingProductIds(connection);
            for (Product product : ProductHandler.getInstance().getCollection()) {
                if (!existingProductIds.contains(product.getId())) {
                    product.setId(addElementToDatabase(product, connection, clientHandler).get(1));
                }
            }
            connection.commit();
        } catch (SQLException | IOException e) {
            logger.error("Ошибка записи коллекции в базу данных: ", e);
        }
    }

    private Set<Integer> getExistingProductIds(Connection connection) throws SQLException {
        Set<Integer> productIds = new HashSet<>();
        String query = "SELECT id FROM Product";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                productIds.add(resultSet.getInt("id"));
            }
        }
        return productIds;
    }

    public ArrayList<Integer> writeObjectToDatabase(Product product, ClientHandler clientHandler) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            ArrayList<Integer> ids = addElementToDatabase(product, connection, clientHandler);
            connection.commit();
            return ids;
        } catch (SQLException | IOException e) {
            logger.error("Ошибка записи объекта в базу данных: ", e);
            return new ArrayList<>(Arrays.asList(-1, -1));
        }
    }

    public ArrayList<Integer> addElementToDatabase(Product product, Connection connection, ClientHandler clientHandler) {
        ArrayList<Integer> ids = new ArrayList<>();
        try {
            int coordinatesId = insertCoordinates(product, connection);
            int addressId = insertAddressIfExists(product, connection);

            int manufacturerId = insertOrganization(product, connection, addressId);
            ids.add(manufacturerId);

            int productId = insertProduct(product, connection, coordinatesId, manufacturerId);
            ids.add(productId);

            upsertCreator(productId, clientHandler.getUserId(), connection);

        } catch (SQLException e) {
            logger.error("Ошибка добавления объекта: ", e);
        }
        return ids;
    }

    private int insertCoordinates(Product product, Connection connection) throws SQLException {
        String query = "INSERT INTO Coordinates (x, y) VALUES (?, ?) RETURNING id";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setFloat(1, product.getCoordinates().getX());
            statement.setInt(2, product.getCoordinates().getY());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return -1;
    }

    private int insertAddressIfExists(Product product, Connection connection) throws SQLException {
        if (product.getManufacturer().getOfficialAddress() != null) {
            String query = "INSERT INTO Address (street, zip_code) VALUES (?, ?) RETURNING id";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, product.getManufacturer().getOfficialAddress().getStreet());
                statement.setString(2, product.getManufacturer().getOfficialAddress().getZipCode());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    private int insertOrganization(Product product, Connection connection, int addressId) throws SQLException {
        String query = "INSERT INTO Organization (name, employees_count, type, official_address_id) " +
                "VALUES (?, ?, CAST(? AS organization_type_enum), ?) RETURNING id";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getManufacturer().getName());
            statement.setLong(2, product.getManufacturer().getEmployeesCount());
            statement.setString(3, product.getManufacturer().getType() != null ? product.getManufacturer().getType().toString() : null);
            if (addressId != -1) {
                statement.setInt(4, addressId);
            } else {
                statement.setNull(4, Types.INTEGER);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return -1;
    }

    private int insertProduct(Product product, Connection connection, int coordinatesId, int manufacturerId) throws SQLException {
        String query = "INSERT INTO Product (name, coordinates_id, creation_date, price, part_number, manufacture_cost, unit_of_measure, manufacturer_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, CAST(? AS unit_of_measure_enum), ?) RETURNING id";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setInt(2, coordinatesId);
            statement.setDate(3, java.sql.Date.valueOf(product.getCreationDate()));
            statement.setInt(4, product.getPrice());
            statement.setString(5, product.getPartNumber());
            if (product.getManufactureCost() != null) {
                statement.setFloat(6, product.getManufactureCost());
            } else {
                statement.setNull(6, Types.FLOAT);
            }
            statement.setString(7, product.getUnitOfMeasure() != null ? product.getUnitOfMeasure().toString() : null);
            statement.setInt(8, manufacturerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return -1;
    }

    private void upsertCreator(int productId, long userId, Connection connection) throws SQLException {
        String query = "INSERT INTO Creator (product_id, user_id) VALUES (?, ?) ON CONFLICT (product_id) DO NOTHING";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.setLong(2, userId);
            statement.executeUpdate();
        }
    }

    // Метод для аутентификации пользователя
    public long authUser(String name, char[] passwd) {
        String query = "SELECT id, passwd_hash, passwd_salt FROM \"User\" WHERE name = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String passwdHash = resultSet.getString("passwd_hash");
                    String passwdSalt = resultSet.getString("passwd_salt");
                    String inputPasswdHash = PasswordHandler.hashPassword(passwd, passwdSalt);

                    if (passwdHash.equals(inputPasswdHash)) {
                        return resultSet.getLong("id");
                    }
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Ошибка при аутентификации пользователя: ", e);
        }
        return -1;
    }

    // Метод для регистрации нового пользователя
    public long regUser(String name, char[] passwd) {
        String checkUserQuery = "SELECT COUNT(*) FROM \"User\" WHERE name = ?";
        String insertUserQuery = "INSERT INTO \"User\" (name, passwd_hash, passwd_salt) VALUES (?, ?, ?)";

        try (Connection connection = getConnection()) {
            // Проверяем, существует ли пользователь с таким именем
            try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery)) {
                checkUserStmt.setString(1, name);
                try (ResultSet resultSet = checkUserStmt.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        return -1; // Пользователь уже существует
                    }
                }
            }



            // Генерируем соль и хэш пароля
            String passwdSalt = PasswordHandler.generateSalt();
            String passwdHash = PasswordHandler.hashPassword(passwd, passwdSalt);

            // Вставляем нового пользователя
            try (PreparedStatement insertUserStmt = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertUserStmt.setString(1, name);
                insertUserStmt.setString(2, passwdHash);
                insertUserStmt.setString(3, passwdSalt);

                int rowsAffected = insertUserStmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = insertUserStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getLong(1);
                        }
                    }
                }
            }

        } catch (SQLException | IOException e) {
            logger.error("Ошибка при регистрации пользователя: ", e);
        }
        return -1;
    }

    // Метод для очистки продуктов пользователя
    public List<Integer> clearProductsForUser(ClientHandler clientHandler) {
        long userId = clientHandler.getUserId();
        List<Integer> deletedProductIds = new ArrayList<>();
        String selectProductIdsQuery = "SELECT product_id FROM Creator WHERE user_id = ?";
        String deleteProductsQuery = "DELETE FROM Product WHERE id = ANY (?)";

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            // Получаем идентификаторы продуктов пользователя
            try (PreparedStatement selectStmt = connection.prepareStatement(selectProductIdsQuery)) {
                selectStmt.setLong(1, userId);
                try (ResultSet resultSet = selectStmt.executeQuery()) {
                    while (resultSet.next()) {
                        deletedProductIds.add(resultSet.getInt("product_id"));
                    }
                }
            }

            // Удаляем продукты пользователя
            if (!deletedProductIds.isEmpty()) {
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteProductsQuery)) {
                    Array idsArray = connection.createArrayOf("INTEGER", deletedProductIds.toArray());
                    deleteStmt.setArray(1, idsArray);
                    deleteStmt.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException | IOException e) {
            logger.error("Ошибка при очистке продуктов пользователя: ", e);
        }
        return deletedProductIds;
    }

    // Метод для проверки принадлежности продукта пользователю
    public boolean isProductOwnedByUser(int productId, ClientHandler clientHandler) {
        String query = "SELECT COUNT(*) FROM Creator WHERE product_id = ? AND user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, productId);
            statement.setLong(2, clientHandler.getUserId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Ошибка при проверке принадлежности продукта: ", e);
        }
        return false;
    }

    // Метод для обновления продукта
    public boolean updateProduct(Product product) {
        String updateOrganizationQuery = "UPDATE Organization SET name = ?, employees_count = ?, type = CAST(? AS organization_type_enum) WHERE id = ?";
        String updateCoordinatesQuery = "UPDATE Coordinates SET x = ?, y = ? WHERE id = ?";
        String updateProductQuery = "UPDATE Product SET name = ?, creation_date = ?, price = ?, part_number = ?, manufacture_cost = ?, unit_of_measure = CAST(? AS unit_of_measure_enum), manufacturer_id = ? WHERE id = ?";

        // Открываем connection внутри try-with-resources
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            // Обновляем организацию (manufacturer)
            try (PreparedStatement stmt = connection.prepareStatement(updateOrganizationQuery)) {
                stmt.setString(1, product.getManufacturer().getName());
                stmt.setLong(2, product.getManufacturer().getEmployeesCount());
                if (product.getManufacturer().getType() != null) {
                    stmt.setString(3, product.getManufacturer().getType().toString());
                } else {
                    stmt.setNull(3, Types.VARCHAR);
                }
                stmt.setLong(4, product.getManufacturer().getId());
                stmt.executeUpdate();
            }

            // Обновляем координаты
            try (PreparedStatement stmt = connection.prepareStatement(updateCoordinatesQuery)) {
                stmt.setFloat(1, product.getCoordinates().getX());
                stmt.setInt(2, product.getCoordinates().getY());
                stmt.setInt(3, product.getId());
                stmt.executeUpdate();
            }

            // Обновляем продукт
            try (PreparedStatement stmt = connection.prepareStatement(updateProductQuery)) {
                stmt.setString(1, product.getName());
                stmt.setDate(2, java.sql.Date.valueOf(product.getCreationDate()));
                stmt.setInt(3, product.getPrice());
                stmt.setString(4, product.getPartNumber());
                if (product.getManufactureCost() != null) {
                    stmt.setFloat(5, product.getManufactureCost());
                } else {
                    stmt.setNull(5, Types.FLOAT);
                }
                if (product.getUnitOfMeasure() != null) {
                    stmt.setString(6, product.getUnitOfMeasure().toString());
                } else {
                    stmt.setNull(6, Types.VARCHAR);
                }
                stmt.setLong(7, product.getManufacturer().getId());
                stmt.setInt(8, product.getId());
                stmt.executeUpdate();
            }

            // Коммитим транзакцию
            connection.commit();
            return true;
        } catch (SQLException | IOException e) {
            logger.error("Ошибка при обновлении продукта: ", e);
            // Откат транзакции в случае ошибки
            try (Connection connection = getConnection()) {
                connection.rollback();
            } catch (SQLException | IOException ex) {
                logger.error("Ошибка при откате транзакции: ", ex);
            }
        }
        return false;
    }

    // Метод для удаления продукта по его ID
    public boolean removeProductById(int productId, ClientHandler clientHandler) {
        String deleteProductQuery = "DELETE FROM Product WHERE id = ? AND id IN (SELECT product_id FROM Creator WHERE user_id = ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteProductQuery)) {

            statement.setInt(1, productId);
            statement.setLong(2, clientHandler.getUserId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | IOException e) {
            logger.error("Ошибка при удалении продукта: ", e);
        }
        return false;
    }

    // Метод для извлечения продукта из ResultSet
    private Product extractProductFromResultSet(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        Coordinates coordinates = new Coordinates(resultSet.getFloat("x"), resultSet.getInt("y"));
        LocalDate creationDate = resultSet.getDate("creation_date").toLocalDate();
        Integer price = resultSet.getInt("price");
        String partNumber = resultSet.getString("part_number");
        if (resultSet.wasNull()) {
            partNumber = null;
        }
        Float manufactureCost = resultSet.getFloat("manufacture_cost");
        if (resultSet.wasNull()) {
            manufactureCost = null;
        }
        UnitOfMeasure unitOfMeasure = resultSet.getString("unit_of_measure") != null
                ? UnitOfMeasure.valueOf(resultSet.getString("unit_of_measure"))
                : null;

        // Извлечение данных для объекта Organization
        long manufacturerId = resultSet.getLong("manufacturer_id");
        String manufacturerName = resultSet.getString("manufacturer_name");
        long employeesCount = resultSet.getLong("employees_count");
        OrganizationType type = resultSet.getString("type") != null
                ? OrganizationType.valueOf(resultSet.getString("type"))
                : null;

        // Извлечение данных для объекта Address (может быть null)
        String street = resultSet.getString("address_street");
        String zipCode = resultSet.getString("address_zip_code");
        Address officialAddress = null;
        if (street != null && zipCode != null) {
            officialAddress = new Address(street, zipCode);
        }

        // Создание объекта Organization
        Organization manufacturer = new Organization(manufacturerId, manufacturerName, employeesCount, type, officialAddress);

        // Создание объекта Product
        return new Product(id, name, coordinates, creationDate, price, partNumber, manufactureCost, unitOfMeasure, manufacturer);
    }
}
