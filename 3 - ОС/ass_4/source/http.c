#include "http.h"

#include <linux/errno.h>
#include <linux/kernel.h>
#include <linux/slab.h>
#include <linux/string.h>
#include <linux/ctype.h>
#include <linux/limits.h>
#include <linux/stdarg.h>

#define VTFS_HTTP_DEFAULT_IP   "127.0.0.1"
#define VTFS_HTTP_DEFAULT_PORT 8080

static char vtfs_server_ip[64] = VTFS_HTTP_DEFAULT_IP;
static int vtfs_server_port = VTFS_HTTP_DEFAULT_PORT;

int vtfs_http_set_endpoint(const char *host, u16 port)
{
  if (host && host[0] != '\0')
    strscpy(vtfs_server_ip, host, sizeof(vtfs_server_ip));
  else
    strscpy(vtfs_server_ip, VTFS_HTTP_DEFAULT_IP, sizeof(vtfs_server_ip));

  if (port != 0)
    vtfs_server_port = (int)port;
  else
    vtfs_server_port = VTFS_HTTP_DEFAULT_PORT;

  return 0;
}

static size_t vtfs_args_encoded_len(size_t arg_size, va_list args)
{
  size_t extra = 0;
  size_t i;

  for (i = 0; i < arg_size; i++) {
    const char *k = va_arg(args, const char *);
    const char *v = va_arg(args, const char *);
    extra += 1; /* & */
    extra += strlen(k ? k : "");
    extra += 1; /* = */
    extra += strlen(v ? v : "");
  }

  return extra;
}

static int vtfs_fill_request_common(struct kvec *vec,
                                    const char *token,
                                    const char *method,
                                    const char *http_method,
                                    const char *body,
                                    size_t body_len,
                                    size_t arg_size,
                                    va_list args)
{
  char port_buf[16];
  size_t args_len;
  size_t need;
  size_t off = 0;
  char *request_buffer;
  va_list args_copy;
  size_t i;

  if (!vec || !token || !method || !http_method)
    return -EINVAL;

  if (arg_size > 0) {
    va_copy(args_copy, args);
    args_len = vtfs_args_encoded_len(arg_size, args_copy);
    va_end(args_copy);
  } else {
    args_len = 0;
  }

  scnprintf(port_buf, sizeof(port_buf), "%d", vtfs_server_port);

  need = strlen(http_method) + 1 + strlen(" /api/") + strlen(method) +
         strlen("?token=") + strlen(token) + args_len +
         strlen(" HTTP/1.1\r\nHost: ") + strlen(vtfs_server_ip) + 1 + strlen(port_buf) +
         strlen("\r\nConnection: close\r\n") + 1;

  if (body) {
    char len_buf[32];
    scnprintf(len_buf, sizeof(len_buf), "%zu", body_len);
    need += strlen("Content-Type: application/x-www-form-urlencoded\r\n") +
            strlen("Content-Length: ") + strlen(len_buf) + strlen("\r\n") +
            strlen("\r\n") + body_len;
  } else {
    need += strlen("\r\n");
  }

  request_buffer = kmalloc(need + 1, GFP_KERNEL);
  if (!request_buffer)
    return -ENOMEM;

  off += scnprintf(request_buffer + off, need + 1 - off,
                   "%s /api/%s?token=%s", http_method, method, token);

  for (i = 0; i < arg_size; i++) {
    const char *k = va_arg(args, const char *);
    const char *v = va_arg(args, const char *);
    off += scnprintf(request_buffer + off, need + 1 - off,
                     "&%s=%s", k ? k : "", v ? v : "");
  }

  off += scnprintf(request_buffer + off, need + 1 - off,
                   " HTTP/1.1\r\nHost: %s:%s\r\nConnection: close\r\n",
                   vtfs_server_ip, port_buf);

  if (body) {
    off += scnprintf(request_buffer + off, need + 1 - off,
                     "Content-Type: application/x-www-form-urlencoded\r\n"
                     "Content-Length: %zu\r\n\r\n",
                     body_len);
    if (off + body_len > need) {
      kfree(request_buffer);
      return -ENOSPC;
    }
    memcpy(request_buffer + off, body, body_len);
    off += body_len;
    request_buffer[off] = '\0';
  } else {
    off += scnprintf(request_buffer + off, need + 1 - off, "\r\n");
  }

  memset(vec, 0, sizeof(*vec));
  vec->iov_base = request_buffer;
  vec->iov_len = off;

  return 0;
}

static int fill_request(struct kvec *vec, const char *token, const char *method,
                        size_t arg_size, va_list args)
{
  return vtfs_fill_request_common(vec, token, method, "GET", NULL, 0, arg_size, args);
}

static int fill_request_body(struct kvec *vec, const char *token, const char *method,
                             const char *body, size_t body_len)
{
  va_list empty;
  memset(&empty, 0, sizeof(empty));
  return vtfs_fill_request_common(vec, token, method, "POST", body, body_len, 0, empty);
}

static int receive_all(struct socket *sock, char *buffer, size_t buffer_size)
{
  struct msghdr hdr;
  struct kvec vec;
  int read = 0;

  while ((size_t)read < buffer_size) {
    int ret;
    memset(&hdr, 0, sizeof(hdr));
    memset(&vec, 0, sizeof(vec));
    vec.iov_base = buffer + read;
    vec.iov_len = buffer_size - (size_t)read;

    ret = kernel_recvmsg(sock, &hdr, &vec, 1, vec.iov_len, 0);
    if (ret == 0)
      break;
    if (ret < 0)
      return ret;

    read += ret;
  }

  return read;
}

static int64_t parse_http_response(char *raw_response, size_t raw_response_size,
                                   char *response, size_t response_size)
{
  char *buffer = raw_response;
  int length = -1;

  {
    char *status_line = strsep(&buffer, "\r");
    char *status_code;

    if (!status_line || !buffer)
      return -EBADMSG;

    strsep(&status_line, " ");
    status_code = strsep(&status_line, " ");
    if (!status_code)
      return -EBADMSG;

    if (strcmp(status_code, "200") != 0)
      return -EIO;
  }

  while (1) {
    char *header;

    if (!buffer)
      return -EBADMSG;

    header = strsep(&buffer, "\r");
    if (!header || !buffer)
      return -EBADMSG;

    if (*buffer == '\n')
      buffer++;

    if (strcmp(header, "") == 0)
      break;

    if (!strncasecmp(header, "Content-Length: ", 16)) {
      if (kstrtoint(header + 16, 10, &length) != 0)
        return -EBADMSG;
    }
  }

  if (length < (int)sizeof(int64_t))
    return -EBADMSG;

  if ((size_t)(buffer - raw_response) + (size_t)length > raw_response_size)
    return -EBADMSG;

  {
    int64_t return_value;
    int payload_len = length - (int)sizeof(int64_t);

    memcpy(&return_value, buffer, sizeof(return_value));
    buffer += sizeof(return_value);

    if ((size_t)payload_len > response_size)
      return -ENOSPC;

    if (payload_len > 0 && response)
      memcpy(response, buffer, (size_t)payload_len);

    return return_value;
  }
}

static int64_t vtfs_http_call_kvec(struct kvec *kvec,
                                   char *response_buffer,
                                   size_t buffer_size)
{
  struct socket *sock;
  struct sockaddr_in s_addr;
  struct msghdr msg;
  int64_t error;
  size_t raw_buffer_size;
  char *raw_response_buffer;
  int read_bytes;

  error = sock_create_kern(&init_net, AF_INET, SOCK_STREAM, IPPROTO_TCP, &sock);
  if (error < 0)
    return error;

  s_addr.sin_family = AF_INET;
  s_addr.sin_addr.s_addr = in_aton(vtfs_server_ip);
  s_addr.sin_port = htons(vtfs_server_port);

  error = kernel_connect(sock, (struct sockaddr *)&s_addr, sizeof(s_addr), 0);
  if (error != 0) {
    sock_release(sock);
    return error;
  }

  memset(&msg, 0, sizeof(msg));
  error = kernel_sendmsg(sock, &msg, kvec, 1, kvec->iov_len);
  kfree(kvec->iov_base);
  kvec->iov_base = NULL;
  kvec->iov_len = 0;

  if (error < 0) {
    kernel_sock_shutdown(sock, SHUT_RDWR);
    sock_release(sock);
    return error;
  }

  raw_buffer_size = buffer_size + 4096;
  raw_response_buffer = kmalloc(raw_buffer_size, GFP_KERNEL);
  if (!raw_response_buffer) {
    kernel_sock_shutdown(sock, SHUT_RDWR);
    sock_release(sock);
    return -ENOMEM;
  }

  read_bytes = receive_all(sock, raw_response_buffer, raw_buffer_size);
  kernel_sock_shutdown(sock, SHUT_RDWR);
  sock_release(sock);

  if (read_bytes < 0) {
    kfree(raw_response_buffer);
    return read_bytes;
  }

  error = parse_http_response(raw_response_buffer, (size_t)read_bytes,
                              response_buffer, buffer_size);
  kfree(raw_response_buffer);
  return error;
}

int64_t vtfs_http_call(const char *token, const char *method,
                       char *response_buffer, size_t buffer_size,
                       size_t arg_size, ...)
{
  struct kvec kvec;
  va_list args;
  int error;

  va_start(args, arg_size);
  error = fill_request(&kvec, token, method, arg_size, args);
  va_end(args);
  if (error)
    return error;

  return vtfs_http_call_kvec(&kvec, response_buffer, buffer_size);
}

int64_t vtfs_http_call_body(const char *token, const char *method,
                            const char *body, size_t body_len,
                            char *response_buffer, size_t buffer_size)
{
  struct kvec kvec;
  int error;

  error = fill_request_body(&kvec, token, method, body, body_len);
  if (error)
    return error;

  return vtfs_http_call_kvec(&kvec, response_buffer, buffer_size);
}

void encode(const char *src, char *dst)
{
  while (*src != '\0') {
    if (isalnum(*src)) {
      *dst++ = *src;
    } else {
      sprintf(dst, "%%%02X", (unsigned char)*src);
      dst += 3;
    }
    src++;
  }
  *dst = '\0';
}
