#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "signal.h"


int
main(int argc, char *argv[])
{
  if(argc != 3){
    fprintf(2, "usage: kill2 pid signum\n");
    exit(1);
  }

  int pid = atoi(argv[1]);
  int sig = atoi(argv[2]);

  if(kill2(pid, sig) < 0){
    fprintf(2, "kill2: failed (pid=%d, sig=%d)\n", pid, sig);
    exit(1);
  }

  exit(0);
}
