#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "signal.h"

int
main(int argc, char *argv[])
{
  printf("traise: pid=%d, сейчас вызову raise(SIGINT)\n", getpid());

  raise(SIGINT);

  printf("ЕСЛИ ТЫ ЭТО ВИДИШЬ — SIGINT НЕ УБИЛ ПРОЦЕСС!\n");
  exit(0);
}
