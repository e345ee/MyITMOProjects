#include "kernel/types.h"
#include "kernel/stat.h"
#include "kernel/signal.h"
#include "user/user.h"
#include "kernel/fcntl.h"

void
xfsz_handler(int sig)
{
  printf("handler: got SIGXFSZ=%d\n", sig);
  exit(0);
}

int
main(void)
{
  printf("tsigxfsz: installing SIGXFSZ handler\n");
  signal(SIGXFSZ, xfsz_handler);

  int old = setfsizelimit(100);
  printf("tsigxfsz: setfsizelimit(100), old=%d\n", old);

  int fd = open("bigfile", O_CREATE | O_WRONLY);
  if(fd < 0){
    printf("tsigxfsz: open failed\n");
    exit(1);
  }

  char buf[64];
  for(int i = 0; i < sizeof(buf); i++)
    buf[i] = 'A';

  int r = write(fd, buf, 64);
  printf("tsigxfsz: first write returned %d\n", r);

  printf("tsigxfsz: second write (should trigger SIGXFSZ)\n");
  r = write(fd, buf, 64);
  printf("tsigxfsz: second write returned %d (unexpected)\n", r);

  close(fd);
  exit(0);
}
