#include "types.h"
#include "param.h"
#include "memlayout.h"
#include "riscv.h"
#include "spinlock.h"
#include "proc.h"
#include "defs.h"
#include "signal.h"

static int is_term_signal(int s) {
  return (s == SIGHUP ||
          s == SIGINT ||
          s == SIGQUIT ||
          s == SIGILL ||
          s == SIGABRT ||
          s == SIGFPE ||
          s == SIGSEGV ||
          s == SIGPIPE ||
          s == SIGALRM ||
          s == SIGTERM ||
          s == SIGUSR1 ||
          s == SIGTRAP ||
          s == SIGBUS ||
          s == SIGUSR2 ||
          s == SIGSYS ||
          s == SIGXCPU ||
          s == SIGXFSZ);
}

static int is_stop_signal(int s) {
  return (s == SIGSTOP ||
          s == SIGTSTP ||
          s == SIGTTIN ||
          s == SIGTTOU);
}

static int is_cont_signal(int s) {
  return s == SIGCONT;
}
uint64
sys_signal(void)
{
  int signum;
  uint64 handler_addr;
  struct proc *p = myproc();

  argint(0, &signum);
  argaddr(1, &handler_addr);

  //printf("KERNEL sys_signal: pid=%d signum=%d handler_addr=0x%p\n",
         //p->pid, signum, (void*)handler_addr);

  if(signum <= 0 || signum >= NSIG){
    //printf("KERNEL sys_signal: invalid signum=%d\n", signum);
    return -1;
  }

  if(signum == SIGKILL || is_stop_signal(signum)){
    //printf("KERNEL sys_signal: signum=%d is forbidden to change\n", signum);
    return -1;
  }

  sighandler_t old = p->sighandlers[signum];

  //printf("KERNEL sys_signal: old handler for signum=%d was 0x%p, disposition=%d\n",
         //signum, old, p->sigdisposition[signum]);

  p->sighandlers[signum]    = (sighandler_t)handler_addr;
  p->sigdisposition[signum] = SIG_USER;

  //printf("KERNEL sys_signal: final state signum=%d handler=0x%p disp=%d\n",
         //signum,
       //  (void*)p->sighandlers[signum],
        // p->sigdisposition[signum]);

  return (uint64)old;
}


uint64
sys_sigprocmask(void)
{
  uint mask;
  struct proc *p = myproc();
  argint(0, (int*)&mask);

  uint old = p->sigmask;

  uint forbidden = (1 << SIGKILL) |
                 (1 << SIGSTOP) |
                 (1 << SIGTSTP) |
                 (1 << SIGTTIN) |
                 (1 << SIGTTOU);
  mask &= ~forbidden;

  p->sigmask = mask;
  return old;
}

uint64
sys_kill2(void)
{
  int pid, signum;
  argint(0, &pid);
  argint(1, &signum);
  return sigsend(pid, signum);   
}

uint64
sys_raise(void)
{
  int signum;
  struct proc *p = myproc();
  argint(0, &signum);
  return sigsend(p->pid, signum);
}

uint64
sys_sigreturn(void)
{
  struct proc *p = myproc();
  *(p->trapframe) = p->sig_tf_backup;
  p->handling_signal = 0;
  return p->trapframe->a0;
}

int
sigsend(int pid, int signum)
{
  if(signum <= 0 || signum >= NSIG)
    return -1;

  struct proc *p = find_proc_by_pid(pid);
  if(p == 0)
    return -1;

  if (!is_stop_signal(signum) && !is_cont_signal(signum))
    p->sigpending |= (1 << signum);

  if (signum == SIGKILL) {
    p->killed = 1;
    if (p->state == SLEEPING)
      p->state = RUNNABLE;
  } else if (is_stop_signal(signum)) {
    p->stopped = 1;
  } else if (is_cont_signal(signum)) {
    p->stopped = 0;
    if (p->state == SLEEPING)
      p->state = RUNNABLE;
  }

  release(&p->lock);
  return 0;
}


void
handle_signals(struct proc *p)
{
  if (p->handling_signal)
    return;

  uint active = p->sigpending & ~p->sigmask;
  if (active == 0)
    return;

  int signum = 0;
  for (int i = 1; i < NSIG; i++) {
    if (active & (1 << i)) {
      signum = i;
      break;
    }
  }

  //printf("KERNEL handle_signals: pid=%d signum=%d disp=%d mask=0x%x pending=0x%x\n",
         //p->pid, signum, p->sigdisposition[signum], p->sigmask, p->sigpending);


  p->sigpending &= ~(1 << signum);

  if (signum == SIGCHLD) {
    if (p->sigdisposition[signum] == SIG_USER) {
      p->sig_tf_backup = *(p->trapframe);
      p->handling_signal = 1;
      p->trapframe->epc = (uint64)p->sighandlers[signum];
      p->trapframe->a0  = signum;
    }
    return;
  }

  if (signum == SIGKILL) {
    p->killed = 1;
    return;
  }


  if (signum == SIGCHLD && p->sigdisposition[signum] == SIG_DFL) {
    return;
  }

  if (p->sigdisposition[signum] == SIG_DFL &&
      is_term_signal(signum)) {
    p->killed = 1;
    return;
  }

  if (p->sigdisposition[signum] == SIG_USER) {

    p->sig_tf_backup = *(p->trapframe);
    p->handling_signal = 1;

    p->trapframe->epc = (uint64)p->sighandlers[signum];
    p->trapframe->a0  = signum;

    return;
  }

}
