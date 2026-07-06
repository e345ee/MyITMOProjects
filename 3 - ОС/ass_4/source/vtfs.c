#include <linux/fs.h>
#include <linux/fcntl.h>
#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/printk.h>
#include <linux/processor.h>
#include <linux/slab.h>
#include <linux/stat.h>
#include <linux/string.h>
#include <linux/uaccess.h>
#include <linux/version.h>
#include <linux/user_namespace.h>

#include "http.h"
#include "vtfs_backend.h"

#define MODULE_NAME "vtfs"

MODULE_LICENSE("GPL");
MODULE_AUTHOR("secs-dev");
MODULE_DESCRIPTION("A simple FS kernel module");

#if LINUX_VERSION_CODE >= KERNEL_VERSION(6, 3, 0)
#define VTFS_USE_MNT_IDMAP 1
#endif

#ifdef VTFS_USE_MNT_IDMAP
#define VTFS_CREATE_SIG int vtfs_create(struct mnt_idmap *idmap, struct inode *parent_inode, \
                                       struct dentry *child_dentry, umode_t mode, bool bflag)
#define VTFS_MKDIR_SIG  int vtfs_mkdir(struct mnt_idmap *idmap, struct inode *parent_inode, \
                                      struct dentry *child_dentry, umode_t mode)
#define VTFS_SETATTR_SIG int vtfs_setattr(struct mnt_idmap *idmap, struct dentry *dentry, struct iattr *iattr)
#define VTFS_INODE_INIT_OWNER(inode, dir, mode) inode_init_owner(&nop_mnt_idmap, inode, dir, mode)
#else
#define VTFS_CREATE_SIG int vtfs_create(struct user_namespace *idmap, struct inode *parent_inode, \
                                       struct dentry *child_dentry, umode_t mode, bool bflag)
#define VTFS_MKDIR_SIG  int vtfs_mkdir(struct user_namespace *idmap, struct inode *parent_inode, \
                                      struct dentry *child_dentry, umode_t mode)
#define VTFS_SETATTR_SIG int vtfs_setattr(struct user_namespace *idmap, struct dentry *dentry, struct iattr *iattr)
#define VTFS_INODE_INIT_OWNER(inode, dir, mode) inode_init_owner(&init_user_ns, inode, dir, mode)
#endif

#define LOG(fmt, ...) pr_info("[" MODULE_NAME "]: " fmt, ##__VA_ARGS__)

struct vtfs_mount_cfg {
  bool use_ram;
  char *token;
  char *host;
  u16 port;
  bool has_port;
};

static inline struct vtfs_backend *VTFS_B(struct super_block *sb)
{
  return (struct vtfs_backend *)sb->s_fs_info;
}

extern const struct vtfs_backend_ops vtfs_ram_ops;
extern const struct vtfs_backend_ops vtfs_http_ops;

static void vtfs_apply_attr(struct inode *inode, const struct vtfs_attr *attr)
{
  if (!inode || !attr)
    return;

  inode->i_mode = (inode->i_mode & S_IFMT) | (attr->mode & 0777);
  if (attr->type == VTFS_DIR) {
    i_size_write(inode, 0);
    set_nlink(inode, attr->nlink ? attr->nlink : 2);
  } else {
    i_size_write(inode, (loff_t)attr->size);
    set_nlink(inode, attr->nlink ? attr->nlink : 1);
  }
}

static void vtfs_mount_cfg_free(struct vtfs_mount_cfg *cfg)
{
  if (!cfg)
    return;
  kfree(cfg->token);
  kfree(cfg->host);
  cfg->token = NULL;
  cfg->host = NULL;
}

static int vtfs_cfg_set_string(char **dst, const char *src)
{
  char *tmp;

  tmp = kstrdup(src ? src : "", GFP_KERNEL);
  if (!tmp)
    return -ENOMEM;

  kfree(*dst);
  *dst = tmp;
  return 0;
}

static int vtfs_parse_mount_source(const char *src, struct vtfs_mount_cfg *cfg)
{
  char *work, *p, *part;
  int err = 0;

  if (!cfg)
    return -EINVAL;

  memset(cfg, 0, sizeof(*cfg));
  cfg->port = 8080;

  if (!src || src[0] == '\0') {
    cfg->token = kstrdup("", GFP_KERNEL);
    return cfg->token ? 0 : -ENOMEM;
  }

  work = kstrdup(src, GFP_KERNEL);
  if (!work)
    return -ENOMEM;

  p = work;
  while ((part = strsep(&p, ",")) != NULL) {
    int port_tmp;

    if (part[0] == '\0')
      continue;

    if (!strncmp(part, "ram:", 4)) {
      cfg->use_ram = true;
      err = vtfs_cfg_set_string(&cfg->token, part + 4);
      if (err)
        goto out;
      continue;
    }

    if (!strncmp(part, "backend=", 8)) {
      cfg->use_ram = !strcmp(part + 8, "ram");
      continue;
    }

    if (!strncmp(part, "token=", 6)) {
      err = vtfs_cfg_set_string(&cfg->token, part + 6);
      if (err)
        goto out;
      continue;
    }

    if (!strncmp(part, "host=", 5)) {
      err = vtfs_cfg_set_string(&cfg->host, part + 5);
      if (err)
        goto out;
      continue;
    }

    if (!strncmp(part, "port=", 5)) {
      err = kstrtoint(part + 5, 10, &port_tmp);
      if (err)
        goto out;
      if (port_tmp <= 0 || port_tmp > 65535) {
        err = -EINVAL;
        goto out;
      }
      cfg->port = (u16)port_tmp;
      cfg->has_port = true;
      continue;
    }

    if (!cfg->token) {
      err = vtfs_cfg_set_string(&cfg->token, part);
      if (err)
        goto out;
    }
  }

  if (!cfg->token) {
    cfg->token = kstrdup("", GFP_KERNEL);
    if (!cfg->token)
      err = -ENOMEM;
  }

out:
  kfree(work);
  if (err)
    vtfs_mount_cfg_free(cfg);
  return err;
}

void vtfs_kill_sb(struct super_block *);
struct dentry *vtfs_mount(struct file_system_type *, int, const char *, void *);
int vtfs_fill_super(struct super_block *, void *, int);
struct inode *vtfs_get_inode(struct super_block *, const struct inode *, umode_t, int);
struct dentry *vtfs_lookup(struct inode *, struct dentry *, unsigned int);
int vtfs_iterate(struct file *, struct dir_context *);
VTFS_CREATE_SIG;
int vtfs_unlink(struct inode *, struct dentry *);
VTFS_MKDIR_SIG;
int vtfs_rmdir(struct inode *, struct dentry *);
ssize_t vtfs_read(struct file *, char __user *, size_t, loff_t *);
ssize_t vtfs_write(struct file *, const char __user *, size_t, loff_t *);
int vtfs_open(struct inode *, struct file *);
int vtfs_link(struct dentry *, struct inode *, struct dentry *);
VTFS_SETATTR_SIG;

struct file_system_type vtfs_fs_type = {
  .name = "vtfs",
  .mount = vtfs_mount,
  .kill_sb = vtfs_kill_sb,
};

struct file_operations vtfs_dir_ops = {
  .iterate_shared = vtfs_iterate,
};

static const struct inode_operations vtfs_file_inode_ops = {
  .setattr = vtfs_setattr,
};

struct file_operations vtfs_file_ops = {
  .open = vtfs_open,
  .read = vtfs_read,
  .write = vtfs_write,
  .llseek = default_llseek,
};

struct inode_operations vtfs_inode_ops = {
  .lookup = vtfs_lookup,
  .create = vtfs_create,
  .unlink = vtfs_unlink,
  .mkdir = vtfs_mkdir,
  .rmdir = vtfs_rmdir,
  .link = vtfs_link,
  .setattr = vtfs_setattr,
};

int vtfs_open(struct inode *inode, struct file *filp)
{
  struct vtfs_backend *be = VTFS_B(inode->i_sb);

  if ((filp->f_flags & O_TRUNC) && be && be->ops && be->ops->truncate) {
    int err = be->ops->truncate(be, (vtfs_id)inode->i_ino, 0);
    if (err)
      return err;
    i_size_write(inode, 0);
  }
  return 0;
}

ssize_t vtfs_read(struct file *filp, char __user *ubuf, size_t len, loff_t *off)
{
  struct inode *inode = file_inode(filp);
  struct vtfs_backend *be = VTFS_B(inode->i_sb);
  size_t done = 0;
  int err;

  if (!be || !be->ops || !be->ops->read)
    return -EOPNOTSUPP;

  while (done < len) {
    size_t chunk = min_t(size_t, 4096, len - done);
    char *kbuf = kmalloc(chunk, GFP_KERNEL);
    ssize_t rd;

    if (!kbuf)
      return done ? (ssize_t)done : -ENOMEM;

    rd = be->ops->read(be, (vtfs_id)inode->i_ino, (u64)(*off), kbuf, chunk);
    if (rd < 0) {
      kfree(kbuf);
      return done ? (ssize_t)done : rd;
    }
    if (rd == 0) {
      kfree(kbuf);
      break;
    }

    err = copy_to_user(ubuf + done, kbuf, rd);
    kfree(kbuf);
    if (err)
      return done ? (ssize_t)done : -EFAULT;

    done += (size_t)rd;
    *off += rd;
  }

  return (ssize_t)done;
}

ssize_t vtfs_write(struct file *filp, const char __user *ubuf, size_t len, loff_t *off)
{
  struct inode *inode = file_inode(filp);
  struct vtfs_backend *be = VTFS_B(inode->i_sb);
  size_t done = 0;

  if (!be || !be->ops || !be->ops->write)
    return -EOPNOTSUPP;

  if (filp->f_flags & O_APPEND)
    *off = i_size_read(inode);

  while (done < len) {
    size_t chunk = min_t(size_t, 4096, len - done);
    char *kbuf = kmalloc(chunk, GFP_KERNEL);
    ssize_t wr;

    if (!kbuf)
      return done ? (ssize_t)done : -ENOMEM;

    if (copy_from_user(kbuf, ubuf + done, chunk)) {
      kfree(kbuf);
      return done ? (ssize_t)done : -EFAULT;
    }

    wr = be->ops->write(be, (vtfs_id)inode->i_ino, (u64)(*off), kbuf, chunk);
    kfree(kbuf);
    if (wr < 0)
      return done ? (ssize_t)done : wr;

    done += (size_t)wr;
    *off += wr;
  }

  if (be->ops->getattr) {
    struct vtfs_attr a;
    if (be->ops->getattr(be, (vtfs_id)inode->i_ino, &a) == 0)
      vtfs_apply_attr(inode, &a);
  }

  return (ssize_t)done;
}

VTFS_SETATTR_SIG
{
  struct inode *inode = d_inode(dentry);
  struct vtfs_backend *be = VTFS_B(inode->i_sb);
  int err = 0;
  bool changed = false;

  (void)idmap;

  if (!be || !be->ops)
    return -EIO;

  if (iattr->ia_valid & ATTR_SIZE) {
    if (!be->ops->truncate)
      return -EOPNOTSUPP;

    err = be->ops->truncate(be, (vtfs_id)inode->i_ino, (u64)iattr->ia_size);
    if (err)
      return err;

    i_size_write(inode, iattr->ia_size);
    changed = true;
  }

  if (iattr->ia_valid & ATTR_MODE) {
    if (!be->ops->chmod)
      return -EOPNOTSUPP;

    err = be->ops->chmod(be, (vtfs_id)inode->i_ino, iattr->ia_mode);
    if (err)
      return err;

    inode->i_mode = (inode->i_mode & S_IFMT) | (iattr->ia_mode & 0777);
    changed = true;
  }

  if (changed && be->ops->getattr) {
    struct vtfs_attr a;
    if (be->ops->getattr(be, (vtfs_id)inode->i_ino, &a) == 0)
      vtfs_apply_attr(inode, &a);
  }

  mark_inode_dirty(inode);
  return 0;
}

VTFS_CREATE_SIG
{
  struct vtfs_backend *b = VTFS_B(parent_inode->i_sb);
  vtfs_id new_id;
  struct vtfs_attr attr;
  struct inode *inode;
  int err;

  (void)idmap;
  (void)bflag;

  err = b->ops->create(b, (vtfs_id)parent_inode->i_ino, child_dentry->d_name.name,
                       VTFS_REG, mode, &new_id);
  if (err)
    return err;

  err = b->ops->getattr(b, new_id, &attr);
  if (err)
    return err;

  inode = vtfs_get_inode(parent_inode->i_sb, parent_inode,
                         S_IFREG | (attr.mode & 0777), (int)new_id);
  if (!inode)
    return -ENOMEM;

  inode->i_op = &vtfs_file_inode_ops;
  inode->i_fop = &vtfs_file_ops;
  vtfs_apply_attr(inode, &attr);

  d_instantiate(child_dentry, inode);
  return 0;
}

int vtfs_unlink(struct inode *parent_inode, struct dentry *child_dentry)
{
  struct vtfs_backend *b = VTFS_B(parent_inode->i_sb);
  struct inode *victim = d_inode(child_dentry);
  int err = b->ops->unlink(b, (vtfs_id)parent_inode->i_ino, child_dentry->d_name.name);
  if (err)
    return err;

  if (victim) {
    drop_nlink(victim);
    mark_inode_dirty(victim);
  }

  d_drop(child_dentry);
  return 0;
}

int vtfs_iterate(struct file *flip, struct dir_context *ctx)
{
  struct dentry *dentry = flip->f_path.dentry;
  struct inode *inode = dentry->d_inode;
  struct vtfs_backend *b = VTFS_B(inode->i_sb);
  struct vtfs_dirent *ents;
  int ret = 0;

  ents = kmalloc_array(8, sizeof(*ents), GFP_KERNEL);
  if (!ents)
    return -ENOMEM;

  if (ctx->pos == 0) {
    if (!dir_emit(ctx, ".", 1, inode->i_ino, DT_DIR))
      goto out;
    ctx->pos++;
  }
  if (ctx->pos == 1) {
    ino_t pino = dentry->d_parent->d_inode->i_ino;
    if (!dir_emit(ctx, "..", 2, pino, DT_DIR))
      goto out;
    ctx->pos++;
  }

  while (1) {
    u32 n = 0;
    u64 next_cursor = 0;
    u64 cursor = (ctx->pos >= 2) ? (u64)(ctx->pos - 2) : 0;
    u32 i;
    int err;

    err = b->ops->readdir(b, (vtfs_id)inode->i_ino, cursor,
                          ents, 8, &n, &next_cursor);
    if (err) {
      ret = err;
      goto out;
    }
    if (n == 0)
      goto out;

    for (i = 0; i < n; i++) {
      unsigned char dtype = (ents[i].type == VTFS_DIR) ? DT_DIR : DT_REG;
      if (!dir_emit(ctx, ents[i].name, strlen(ents[i].name), (ino_t)ents[i].id, dtype))
        goto out;
      ctx->pos++;
    }

    if (next_cursor >= cursor)
      ctx->pos = (loff_t)(next_cursor + 2);
  }

out:
  kfree(ents);
  return ret;
}

VTFS_MKDIR_SIG
{
  struct vtfs_backend *be = VTFS_B(parent_inode->i_sb);
  vtfs_id new_id;
  struct vtfs_attr attr;
  struct inode *inode;
  int err;

  (void)idmap;

  err = be->ops->create(be, (vtfs_id)parent_inode->i_ino, child_dentry->d_name.name,
                        VTFS_DIR, mode, &new_id);
  if (err)
    return err;

  err = be->ops->getattr(be, new_id, &attr);
  if (err)
    return err;

  inode = vtfs_get_inode(parent_inode->i_sb, parent_inode,
                         S_IFDIR | (attr.mode & 0777), (int)new_id);
  if (!inode)
    return -ENOMEM;

  inode->i_op = &vtfs_inode_ops;
  inode->i_fop = &vtfs_dir_ops;
  vtfs_apply_attr(inode, &attr);

  d_instantiate(child_dentry, inode);
  inc_nlink(parent_inode);
  mark_inode_dirty(parent_inode);
  return 0;
}

int vtfs_rmdir(struct inode *parent_inode, struct dentry *child_dentry)
{
  struct vtfs_backend *be = VTFS_B(parent_inode->i_sb);
  struct inode *victim = d_inode(child_dentry);
  int err = be->ops->unlink(be, (vtfs_id)parent_inode->i_ino, child_dentry->d_name.name);
  if (err)
    return err;

  if (victim) {
    clear_nlink(victim);
    mark_inode_dirty(victim);
  }
  drop_nlink(parent_inode);
  mark_inode_dirty(parent_inode);

  d_drop(child_dentry);
  return 0;
}

int vtfs_link(struct dentry *old_dentry, struct inode *parent_dir, struct dentry *new_dentry)
{
  struct inode *old_inode = d_inode(old_dentry);
  struct vtfs_backend *be = VTFS_B(parent_dir->i_sb);
  struct vtfs_attr attr;
  vtfs_id old_id;
  int err;

  if (!old_inode)
    return -ENOENT;
  if (S_ISDIR(old_inode->i_mode))
    return -EPERM;
  if (!be || !be->ops || !be->ops->link)
    return -EOPNOTSUPP;

  old_id = (vtfs_id)old_inode->i_ino;
  err = be->ops->link(be, old_id, (vtfs_id)parent_dir->i_ino, new_dentry->d_name.name);
  if (err)
    return err;

  err = be->ops->getattr(be, old_id, &attr);
  if (err)
    return err;

  vtfs_apply_attr(old_inode, &attr);
  mark_inode_dirty(old_inode);

  {
    struct inode *inode = vtfs_get_inode(parent_dir->i_sb, parent_dir,
                                         S_IFREG | (attr.mode & 0777), (int)old_id);
    if (!inode)
      return -ENOMEM;

    inode->i_op = &vtfs_file_inode_ops;
    inode->i_fop = &vtfs_file_ops;
    vtfs_apply_attr(inode, &attr);
    mark_inode_dirty(inode);
    d_instantiate(new_dentry, inode);
  }

  return 0;
}

struct dentry *vtfs_lookup(struct inode *parent_inode, struct dentry *child_dentry,
                           unsigned int flag)
{
  struct vtfs_backend *b = VTFS_B(parent_inode->i_sb);
  vtfs_id child_id;
  struct vtfs_attr attr;
  struct inode *inode;
  int err;

  (void)flag;

  err = b->ops->lookup(b, (vtfs_id)parent_inode->i_ino, child_dentry->d_name.name, &child_id);
  if (err == -ENOENT) {
    d_add(child_dentry, NULL);
    return NULL;
  }
  if (err)
    return ERR_PTR(err);

  err = b->ops->getattr(b, child_id, &attr);
  if (err)
    return ERR_PTR(err);

  inode = vtfs_get_inode(parent_inode->i_sb, parent_inode,
                         (attr.type == VTFS_DIR ? S_IFDIR : S_IFREG) | (attr.mode & 0777),
                         (int)child_id);
  if (!inode)
    return ERR_PTR(-ENOMEM);

  if (attr.type == VTFS_DIR) {
    inode->i_op = &vtfs_inode_ops;
    inode->i_fop = &vtfs_dir_ops;
  } else {
    inode->i_op = &vtfs_file_inode_ops;
    inode->i_fop = &vtfs_file_ops;
  }
  vtfs_apply_attr(inode, &attr);

  return d_splice_alias(inode, child_dentry);
}

struct inode *vtfs_get_inode(struct super_block *sb, const struct inode *dir,
                             umode_t mode, int i_ino)
{
  struct inode *inode = iget_locked(sb, (unsigned long)i_ino);

  if (!inode)
    return NULL;

  inode->i_mode = mode;

  if (S_ISDIR(mode)) {
    inode->i_op = &vtfs_inode_ops;
    inode->i_fop = &vtfs_dir_ops;
    set_nlink(inode, 2);
  } else {
    inode->i_op = &vtfs_file_inode_ops;
    inode->i_fop = &vtfs_file_ops;
  }

  if (!(inode->i_state & I_NEW))
    return inode;

  VTFS_INODE_INIT_OWNER(inode, dir, mode);
  inode->i_ino = i_ino;
  i_size_write(inode, 0);
  if (S_ISDIR(mode))
    set_nlink(inode, 2);
  else
    set_nlink(inode, 1);

  unlock_new_inode(inode);
  return inode;
}

int vtfs_fill_super(struct super_block *sb, void *data, int silent)
{
  struct vtfs_mount_cfg cfg;
  struct vtfs_backend *b = NULL;
  struct inode *inode = NULL;
  int err;

  (void)silent;

  err = vtfs_parse_mount_source((const char *)data, &cfg);
  if (err)
    return err;

  b = kzalloc(sizeof(*b), GFP_KERNEL);
  if (!b) {
    vtfs_mount_cfg_free(&cfg);
    return -ENOMEM;
  }

  b->ops = cfg.use_ram ? &vtfs_ram_ops : &vtfs_http_ops;
  if (!cfg.use_ram)
    vtfs_http_set_endpoint(cfg.host, cfg.has_port ? cfg.port : 0);

  sb->s_fs_info = b;
  sb->s_magic = 0x76667473;
  sb->s_blocksize = PAGE_SIZE;
  sb->s_blocksize_bits = PAGE_SHIFT;

  err = b->ops->init(b, cfg.token);
  if (err)
    goto fail;

  inode = vtfs_get_inode(sb, NULL, S_IFDIR | 0777, (int)b->root_id);
  if (!inode) {
    err = -ENOMEM;
    goto fail;
  }

  inode->i_op = &vtfs_inode_ops;
  inode->i_fop = &vtfs_dir_ops;
  set_nlink(inode, 2);

  sb->s_root = d_make_root(inode);
  inode = NULL;
  if (!sb->s_root) {
    err = -ENOMEM;
    goto fail;
  }

  vtfs_mount_cfg_free(&cfg);
  return 0;

fail:
  if (inode)
    iput(inode);
  if (b) {
    if (b->ops && b->ops->destroy)
      b->ops->destroy(b);
    kfree(b);
    sb->s_fs_info = NULL;
  }
  vtfs_mount_cfg_free(&cfg);
  return err;
}

void vtfs_kill_sb(struct super_block *sb)
{
  struct vtfs_backend *b = VTFS_B(sb);

  /* Let VFS tear down superblock/dentries/inodes first. */
  kill_anon_super(sb);

  if (b) {
    if (b->ops && b->ops->destroy)
      b->ops->destroy(b);
    kfree(b);
    sb->s_fs_info = NULL;
  }

  LOG("vtfs super block destroyed\n");
}

struct dentry *vtfs_mount(struct file_system_type *fs_type, int flags,
                          const char *token, void *data)
{
  struct dentry *ret;

  (void)data;

  ret = mount_nodev(fs_type, flags, (void *)token, vtfs_fill_super);
  if (IS_ERR(ret)) {
    LOG("Can't mount file system: %ld\n", PTR_ERR(ret));
    return ret;
  }

  LOG("Mounted successfully\n");
  return ret;
}

static int __init vtfs_init(void)
{
  int err = register_filesystem(&vtfs_fs_type);
  if (err) {
    LOG("register_filesystem failed: %d\n", err);
    return err;
  }

  LOG("VTFS joined the kernel\n");
  return 0;
}

static void __exit vtfs_exit(void)
{
  int err = unregister_filesystem(&vtfs_fs_type);
  if (err)
    LOG("unregister_filesystem failed: %d\n", err);

  LOG("VTFS left the kernel\n");
}

module_init(vtfs_init);
module_exit(vtfs_exit);
