#ifndef VTFS_HTTP_H
#define VTFS_HTTP_H

#include <linux/inet.h>
#include <linux/net.h>
#include <linux/socket.h>
#include <linux/types.h>
#include <net/sock.h>

int vtfs_http_set_endpoint(const char *host, u16 port);

int64_t vtfs_http_call(const char *token, const char *method,
                       char *response_buffer, size_t buffer_size,
                       size_t arg_size, ...);

int64_t vtfs_http_call_body(const char *token, const char *method,
                            const char *body, size_t body_len,
                            char *response_buffer, size_t buffer_size);

void encode(const char *, char *);

#endif // VTFS_HTTP_H
