#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "user/signal.h"

void pipe_handler(int sig) {
    printf("handler: got SIGPIPE=%d\n", sig);
    sigreturn();
}

int
main(void)
{
    int fds[2];
    pipe(fds);

    signal(SIGPIPE, pipe_handler);

    int pid = fork();
    if(pid == 0){
        close(fds[0]);
        close(fds[1]);
        exit(0);
    }

    close(fds[0]);
    sleep(10);

    printf("parent: writing to broken pipe\n");
    int n = write(fds[1], "abc", 3);
    printf("write returned %d\n", n);

    sleep(20);
    exit(0);
}
