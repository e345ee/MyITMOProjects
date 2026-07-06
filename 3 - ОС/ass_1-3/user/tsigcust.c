#include "kernel/types.h"
#include "kernel/signal.h"
#include "user/user.h"

volatile int got_usr1 = 0;

void
usr1_handler(int sig)
{
  got_usr1 = 1;  
  sigreturn();
}

int
main(void)
{
  int fds[2];
  int parent_pid = getpid();
  int pid;

  if (pipe(fds) < 0) {
    printf("tsigcust: pipe failed\n");
    exit(1);
  }

  printf("tsigcust: installing SIGUSR1 handler in parent\n");
  signal(SIGUSR1, usr1_handler);

  pid = fork();
  if (pid < 0) {
    printf("tsigcust: fork failed\n");
    exit(1);
  }

  if (pid == 0) {
    close(fds[0]); 

    const char *m1 = "child: sending SIGUSR1 to parent\n";
    const char *m2 = "child: exiting\n";

    write(fds[1], m1, strlen(m1));

    kill2(parent_pid, SIGUSR1);

    write(fds[1], m2, strlen(m2));

    close(fds[1]);
    exit(0);
  }

  close(fds[1]); 

  printf("parent: waiting for child & SIGUSR1\n");

  wait(0);

  char buf[128];
  int n;
  while ((n = read(fds[0], buf, sizeof(buf))) > 0) {
    write(1, buf, n);
  }
  close(fds[0]);

  if (got_usr1) {
    printf("parent: handler saw SIGUSR1\n");
  } else {
    printf("parent: no SIGUSR1 received (unexpected)\n");
  }

  printf("parent: done\n");
  exit(0);
}
