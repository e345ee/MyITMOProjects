#include "kernel/types.h"
#include "kernel/signal.h"
#include "user/user.h"

volatile int got_alarm = 0;

void
alrm_handler(int signo)
{
  printf("handler: got SIGALRM=%d\n", signo);
  got_alarm = 1;
}

int
main(void)
{
  printf("setting SIGALRM handler\n");
  signal(SIGALRM, alrm_handler);

  printf("alarm(50)\n");
  alarm(50);

  while(!got_alarm) {
    sleep(1);
  }

  printf("done\n");
  exit(0);
}