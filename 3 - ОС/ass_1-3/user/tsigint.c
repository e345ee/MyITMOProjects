#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "signal.h"

int
main(void)
{
  printf("tsigint_spin: pid=%d\n", getpid());
  while(1){
    printf("spin...\n");
    sleep(50);
    for(volatile int i = 0; i < 1000000; i++) {}
  }
}
