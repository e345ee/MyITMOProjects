#include "kernel/types.h"
#include "kernel/signal.h"
#include "user/user.h"

void segv_handler(int sig) {
  printf("handler: got SIGSEGV=%d\n", sig);
  exit(1);
}

int
main(void)
{
  printf("tsigsegv: setting handler\n");
  signal(SIGSEGV, segv_handler);

  printf("tsigsegv: going to touch bad address\n");
  int *p = (int*)0xFFFFFFFFFFFFFFFFULL;
  *p = 42;

  printf("tsigsegv: after store (should not get here)\n");
  exit(0);
}
