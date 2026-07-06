#include "kernel/types.h"
#include "kernel/signal.h"
#include "user/user.h"

void handler(int sig) {
  printf("child: got SIGHUP=%d\n", sig);
  exit(0);
}

int
main(void)
{
  int pid = fork();
  if(pid == 0){
    signal(SIGHUP, handler);
    printf("child: waiting for SIGHUP...\n");
    while(1) sleep(10);
  }

  sleep(30);
  printf("parent: exiting\n");
  exit(0);
}
