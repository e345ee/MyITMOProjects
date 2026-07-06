#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"

//Создаем 

#if defined(LOTTERY)

typedef unsigned long long u64;

struct result {
    char kind;
    int pid;
    u64 cnt;
};

static u64
burn_for_ticks(int ticks)
{
  int end = uptime() + ticks;
  volatile u64 cnt = 0;

  while(uptime() < end){
    for(int i = 0; i < 20000; i++)
      cnt++;
  }

  return (u64)cnt;
}
void 
write_full(int fd, const void *buf, int n)
{
    const char *p = (const char*)buf;
    int off = 0;
    while(off < n){
        int m = write(fd, p + off, n - off);
        if(m<=0){
            exit(1);
        }
        off += m;
    }
}

void 
read_full(int fd, void *buf, int n)
{
    char *p = (char*)buf;
    int off = 0;
    while(off < n){
        int m = read(fd, p + off, n - off);
        if(m<=0)
            exit(1);
        off += m;
    }
}

int
main(void)
{
    const int NLOW = 10;
    const int DUR_TICKS = 200;
    const int HIGH_TICKETS = 200;
    const int LOW_TICKETS = 1;

    int p[2];
    if(pipe(p) < 0){
        printf("lotterytest: pipe failed\n");
        exit(1);
    }

    int pid = fork();
    if(pid < 0){
        printf("lotterytest: fork failed\n");
        exit(1);
    }
    if (pid == 0){
        close(p[0]);
        if(settickets(HIGH_TICKETS) < 0){
            printf("lotterytest: settickets failed (high)\n");
            exit(1);
        }
        struct result r;
        r.kind = 'H';
        r.pid = getpid();
        r.cnt = burn_for_ticks(DUR_TICKS);
        write_full(p[1], &r, sizeof(r));
        close(p[1]);
        exit(0);
    }

    for(int i = 0; i < NLOW; i++){
        pid = fork();
        if(pid < 0){
            printf("lotterytest: fork failed\n");
            exit(1);
        }
        if(pid == 0){
            close(p[0]);
            if(settickets(LOW_TICKETS) < 0){
                printf("lotterytest: settickets failed (low)\n");
                exit(1);
            }
            struct result r;
            r.kind = 'L';
            r.pid = getpid();
            r.cnt = burn_for_ticks(DUR_TICKS);
            write_full(p[1],&r, sizeof(r));
            close(p[1]);
            exit(0);
        }
    }

    close(p[1]);

    u64 high = 0;
    u64 sumlow = 0;
    u64 maxlow = 0;

    for(int i = 0; i < NLOW + 1; i++){
        struct result r;
        read_full(p[0], &r, sizeof(r));
        if(r.kind == 'H'){
            high = r.cnt;
        } else {
            sumlow += r.cnt;
            if(r.cnt > maxlow)
                maxlow = r.cnt;
        }

    }

    close(p[0]);

    for(int i = 0; i < NLOW + 1; i++)
        wait(0);
    u64 avglow = (NLOW ? (sumlow / (u64)NLOW) : 0);

    printf("lotterytest: high=%p avglow=%p maxlow=%p\n", (void*)high, (void*)avglow, (void*)maxlow);

    if (high > maxlow *3){
        printf("lotterytest: PASS\n");
        exit(0);
    }

    printf("lotterytest: FAIL (not enough advantage)\n");
    exit(1);


}



#else
int 
main(void)
{
    printf("lotterytest: SKIP (not build with LOTTERY scheduler)\n");
    exit(0);
}
#endif