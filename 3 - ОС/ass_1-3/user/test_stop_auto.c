#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "signal.h"

static void
lock_acquire(int lock[2], const char *who)
{
  char ch;
  if(read(lock[0], &ch, 1) != 1){
    fprintf(2, "%s: lock_acquire failed\n", who);
    exit(1);
  }
}

static void
lock_release(int lock[2], const char *who)
{
  char ch = 'X';
  if(write(lock[1], &ch, 1) != 1){
    fprintf(2, "%s: lock_release failed\n", who);
    exit(1);
  }
}

int
main(int argc, char *argv[])
{
  int lock[2];

  if(pipe(lock) < 0){
    fprintf(2, "test_stop_auto: pipe failed\n");
    exit(1);
  }

  if(write(lock[1], "X", 1) != 1){
    fprintf(2, "test_stop_auto: initial token write failed\n");
    exit(1);
  }

  int pid = fork();
  if(pid < 0){
    printf("fork failed\n");
    exit(1);
  }

  if(pid == 0){
    int i = 0;
    int mypid = getpid();
    for(;;){
      lock_acquire(lock, "child");
      printf("[child %d] i=%d\n", mypid, i++);
      lock_release(lock, "child");
      sleep(20);
    }
    exit(0);
  }

  int mypid = getpid();

  lock_acquire(lock, "parent");
  printf("[parent %d] child pid = %d\n", mypid, pid);
  printf("[parent %d] letting child run...\n", mypid);
  lock_release(lock, "parent");

  sleep(200);

  lock_acquire(lock, "parent");
  printf("\n[parent %d] sending SIGSTOP to child\n\n", mypid);
  lock_release(lock, "parent");

  if(kill2(pid, SIGSTOP) < 0){
    lock_acquire(lock, "parent");
    printf("[parent %d] kill2(SIGSTOP) failed, killing child\n", mypid);
    lock_release(lock, "parent");
    kill(pid);
    exit(1);
  }

  lock_acquire(lock, "parent");
  printf("[parent %d] now child MUST STOP printing for 200 ticks\n", mypid);
  lock_release(lock, "parent");

  sleep(200);

  lock_acquire(lock, "parent");
  printf("\n[parent %d] sending SIGCONT to child\n\n", mypid);
  lock_release(lock, "parent");

  if(kill2(pid, SIGCONT) < 0){
    lock_acquire(lock, "parent");
    printf("[parent %d] kill2(SIGCONT) failed, killing child\n", mypid);
    lock_release(lock, "parent");
    kill(pid);
    exit(1);
  }

  lock_acquire(lock, "parent");
  printf("[parent %d] child MUST RESUME printing for 200 ticks\n", mypid);
  lock_release(lock, "parent");

  sleep(200);

  lock_acquire(lock, "parent");
  printf("[parent %d] now killing child with SIGKILL\n", mypid);

  kill2(pid, SIGKILL);
  wait(0);

  printf("[parent %d] done\n", mypid);
  lock_release(lock, "parent");

  exit(0);
}