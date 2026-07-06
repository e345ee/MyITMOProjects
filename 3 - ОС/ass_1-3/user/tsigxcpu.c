#include "kernel/types.h"
#include "kernel/signal.h"
#include "user/user.h"

void
xcpu_handler(int sig)
{
  printf("handler: got SIGXCPU=%d\n", sig);
  exit(0);
}

int
main(void)
{
  printf("tsigxcpu: installing SIGXCPU handler\n");
  signal(SIGXCPU, xcpu_handler);

  int old = setcpulimit(50); 
  printf("tsigxcpu: setcpulimit(50), old=%d\n", old);

  volatile uint64 x = 0;
  while(1){
    x += 1;        
  }

  printf("tsigxcpu: done (unexpected)\n");
  exit(0);
}
