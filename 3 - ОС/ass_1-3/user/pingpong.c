#include "kernel/types.h"
#include "user/user.h"

static const char MSG_PING[] = "ping";
static const char MSG_PONG[] = "pong";
static const char TERMINATOR = '\0';

#define MSG_PING_LEN (sizeof(MSG_PING) - 1)  
#define MSG_PONG_LEN (sizeof(MSG_PONG) - 1)

#define MAX(a,b) ((a) > (b) ? (a) : (b))
#define MSG_BUF_SIZE (MAX(MSG_PING_LEN, MSG_PONG_LEN) + 1)

struct pipes{
    int parent[2];
    int child[2];
};

static void pp_make(struct pipes *p){
    if (pipe(p->parent)<0 || pipe(p->child)<0){
        fprintf(2, "pingpong: pipe() failed\n");
        exit(1);
    }
}

static void pp_close(int fd, const char *who, const char *what){
  if (close(fd) < 0) {
    fprintf(2, "%s: close(%s) failed\n", who, what);
    exit(1);
  }
}

static void pp_read(int fd, void *buf, int n, const char *who){
    if (read(fd, buf, n) != n){
        fprintf(2, "%s: read failed\n", who);
        exit(1);
    }
}

static void pp_write(int fd, const void *buf, int n, const char *who){
        if (write(fd, buf, n) != n){
        fprintf(2, "%s: write failed\n", who);
        exit(1);
    }
}

static int pp_fork(){
    int pid = fork();
    if (pid < 0){
        fprintf(2, "fork failed\n");
        exit(1);
    }
    return pid;
}

static void parent_pipeline(struct pipes *p){
    char buf[MSG_BUF_SIZE];  
    buf[0] = 0;              

    pp_close(p->parent[0], "parent", "parent[0]");  
    pp_close(p->child[1],  "parent", "child[1]");   

 
    pp_write(p->parent[1], MSG_PING, MSG_PING_LEN, "parent");
    pp_read(p->child[0],  buf,       MSG_PONG_LEN, "parent");
    buf[MSG_PONG_LEN] = TERMINATOR;

    printf("%d: got %s\n", getpid(), buf);

    pp_close(p->parent[1], "parent", "parent[1]");
    pp_close(p->child[0],  "parent", "child[0]");

}

static void child_pipeline(struct pipes *p){
    char buf[MSG_BUF_SIZE];
    buf[0] = 0;

  
    pp_close(p->parent[1], "child", "parent[1]");   
    pp_close(p->child[0],  "child", "child[0]");    

 
    pp_read(p->parent[0], buf,       MSG_PING_LEN, "child");
    buf[MSG_PING_LEN] = TERMINATOR;
    printf("%d: got %s\n", getpid(), buf);

    pp_write(p->child[1], MSG_PONG, MSG_PONG_LEN, "child");


    pp_close(p->parent[0], "child", "parent[0]");
    pp_close(p->child[1],  "child", "child[1]");
}


int main(void){
    struct pipes p;
    pp_make(&p);

    int pid = pp_fork();

    if(pid == 0){
        child_pipeline(&p);
        exit(0);
    } else{
        parent_pipeline(&p);
        exit(0);
    }


}




