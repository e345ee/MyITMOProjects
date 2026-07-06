#include "kernel/types.h"
#include "kernel/signal.h"
#include "user/user.h"

void
sigsys_handler(int sig)
{
  printf("handler: got SIGSYS=%d\n", sig);
  sigreturn();
}

int
main(void)
{
  printf("tsigsys: installing SIGSYS handler\n");
  signal(SIGSYS, sigsys_handler);

  printf("tsigsys: calling invalid syscall\n");

  asm volatile(
    "li a7, 999\n" 
    "ecall\n"
  );

  printf("tsigsys: after invalid syscall\n");
  exit(0);
}
