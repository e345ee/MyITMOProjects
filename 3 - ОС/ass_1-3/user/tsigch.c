#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "signal.h"

volatile int got_sigchld = 0;

void
chld_handler(int sig)
{
  printf("parent: got SIGCHLD=%d in handler\n", sig);
  got_sigchld = 1;
  sigreturn();
}

int
main(void)
{
  signal(SIGCHLD, chld_handler);

  int pid = fork();
  if(pid == 0){
    printf("child: exiting (pid=%d)\n", getpid());
    exit(0);
  }

  int status;
  int wpid;

  while(!got_sigchld)
    sleep(1);

  wpid = wait(&status);
  printf("parent: wait() returned pid=%d status=%d\n", wpid, status);

  printf("parent: done, exiting\n");
  exit(0);
}
