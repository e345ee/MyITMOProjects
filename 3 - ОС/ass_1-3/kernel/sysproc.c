#include "types.h"
#include "riscv.h"
#include "defs.h"
#include "param.h"
#include "memlayout.h"
#include "spinlock.h"
#include "proc.h"


uint64
sys_exit(void)
{
  int n;
  argint(0, &n);
  exit(n);
  return 0;  // not reached
}

uint64
sys_getpid(void)
{
  return myproc()->pid;
}

uint64
sys_fork(void)
{
  return fork();
}

uint64
sys_wait(void)
{
  uint64 p;
  argaddr(0, &p);
  return wait(p);
}

uint64
sys_sbrk(void)
{
  uint64 addr;
  int n;

  argint(0, &n);
  addr = myproc()->sz;
  if(growproc(n) < 0)
    return -1;
  return addr;
}

uint64
sys_sleep(void)
{
  int n;
  uint ticks0;

  argint(0, &n);
  if(n < 0)
    n = 0;
  acquire(&tickslock);
  ticks0 = ticks;
  while(ticks - ticks0 < n){
    if(killed(myproc())){
      release(&tickslock);
      return -1;
    }
    sleep(&ticks, &tickslock);
  }
  release(&tickslock);
  return 0;
}

uint64
sys_kill(void)
{
  int pid;

  argint(0, &pid);
  return kill(pid);
}

// return how many clock tick interrupts have occurred
// since start.
uint64
sys_uptime(void)
{
  uint xticks;

  acquire(&tickslock);
  xticks = ticks;
  release(&tickslock);
  return xticks;
}

uint64
sys_dump(void)
{
  dump();
  return 0;
}

uint64
sys_dump2(void)
{
  int pid, regnum;
  uint64 uaddr;

  argint(0, &pid);
  argint(1, &regnum);
  argaddr(2, &uaddr);

  return dump2(pid, regnum, uaddr);
}

uint64
sys_setfg(void)
{
  int pid;
  argint(0, &pid);
  set_fgpid(pid);
  return 0;
}

uint64
sys_alarm(void)
{
  int ticks;
  struct proc *p = myproc();

  argint(0, &ticks);

  if (ticks <= 0) {
    p->alarm_active = 0;
    p->alarm_ticks = 0;
    return 0;
  }

  p->alarm_active = 1;
  p->alarm_ticks  = ticks;

  return 0;
}

uint64
sys_setcpulimit(void)
{
  int limit;
  struct proc *p = myproc();

  argint(0, &limit);

  if (limit < 0)
    return -1;

  int old = p->cpu_limit;
  p->cpu_limit = limit;
  p->cpu_used = 0;  

  return old;
}

uint64
sys_setfsizelimit(void)
{
  int limit;
  struct proc *p = myproc();

  argint(0, &limit);

  if (limit < 0)
    return -1;

  int old = p->fsize_limit;
  p->fsize_limit = limit;

  return old;
}

uint64
sys_ps(void)
{
  proc_ps();
  return 0;
}

uint64
sys_setbg(void)
{
  int bg;
  argint(0, &bg);

  struct proc *p = myproc();
  p->isbg = bg ? 1 : 0;
  return 0;
}

uint64
sys_settickets(void)
{
  int n;
  argint(0, &n);
  if(n < 1) return -1;
  if(n < 1)
    return -1;

  struct proc *p = myproc();
  acquire(&p->lock);
  p->tickets = n;
  release(&p->lock);
  return 0;
}