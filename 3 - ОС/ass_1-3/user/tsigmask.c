#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "user/signal.h"

void chld(int sig) {
  printf("handler: got SIGCHLD\n");
  sigreturn();
}

int
main(void)
{
  printf("parent: installing SIGCHLD handler\n");
  signal(SIGCHLD, chld);

  uint old = sigprocmask(1 << SIGCHLD);

  int pid = fork();
  if(pid == 0){
    sleep(10);
    exit(0);
  }

  sleep(20);

  printf("parent: unmasking SIGCHLD\n");
  sigprocmask(old);   

  sleep(10);

  exit(0);
}
