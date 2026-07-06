#include "types.h"
#include "param.h"
#include "memlayout.h"
#include "riscv.h"
#include "spinlock.h"
#include "proc.h"
#include "defs.h"
#include "signal.h"

struct safe_list {
  struct safe_list *prev;
  struct safe_list *next;
  int ref_count; //счетчик ссылок
  struct proc *p;
};

void safe_list_init(struct safe_list *l); //подготоваливаем узел
void safe_list_acquire(struct safe_list *l); 
void safe_list_relese(struct safe_list *l, struct safe_list *head);
void safe_list_push_tail(struct safe_list *l, struct safe_list *head);
void safe_list_insert_before(struct safe_list *l, struct safe_list *before, struct safe_list *head);
struct safe_list* safe_list_begin(struct safe_list *l);
struct safe_list* safe_list_rbegin(struct safe_list *head);
void safe_list_iterate(struct safe_list **l, struct safe_list *head);
void safe_list_riterate(struct safe_list **l, struct safe_list *head);
void safe_list_remove(struct safe_list *l, struct safe_list *head);
void safe_list_move_to_end(struct safe_list *l, struct safe_list *head);

void
safe_list_init(struct safe_list *l)
{
  l->prev = l;
  l->next = l;
  l->ref_count = 2;
  l->p = 0;
}

void
safe_list_acquire(struct safe_list *l)
{
  l->ref_count++;
}

static void
safe_list_free_node(struct safe_list *l)
{
  if(l == 0)
    return;
  struct proc *p = l->p;
  l->p = 0;
  if(p)
    bd_free(p);
  bd_free(l);
}

void
safe_list_relese(struct safe_list *l, struct safe_list *head)
{
  if(l == head)
    return;

  l->ref_count--;

  if(l->ref_count == 0){
    struct safe_list *n = l->next;
    struct safe_list *p = l->prev;
    safe_list_relese(n, head);
    safe_list_relese(p, head);
    safe_list_free_node(l);
  }
}

void
safe_list_remove(struct safe_list *l, struct safe_list *head)
{
  l->prev->next = l->next;
  l->next->prev = l->prev;

  if(l->next != head)
    safe_list_acquire(l->next);
  if(l->prev != head)
    safe_list_acquire(l->prev);

  l->ref_count -= 2;

  if(l->ref_count == 0)
    safe_list_free_node(l);
}

struct safe_list*
safe_list_next(struct safe_list *l, struct safe_list *head)
{
  if(l->next != head)
    safe_list_acquire(l->next);
  return l->next;
}

struct safe_list*
safe_list_begin(struct safe_list *l)
{
  return safe_list_next(l, l);
}

void
safe_list_iterate(struct safe_list **l, struct safe_list *head)
{
  struct safe_list *next = safe_list_next(*l, head);
  safe_list_relese(*l, head);
  *l = next;
}

void
safe_list_push_tail(struct safe_list *l, struct safe_list *head)
{
  l->ref_count = 2;

  l->next = head;
  l->prev = head->prev;
  head->prev->next = l;
  head->prev = l;
}

void
safe_list_insert_before(struct safe_list *l, struct safe_list *before, struct safe_list *head)
{
  if(before == head){
    safe_list_push_tail(l, head);
    return;
  }

  l->ref_count = 2;

  l->next = before;
  l->prev = before->prev;
  before->prev->next = l;
  before->prev = l;
}

struct safe_list*
safe_list_prev(struct safe_list *l, struct safe_list *head)
{
  if(l->prev != head)
    safe_list_acquire(l->prev);
  return l->prev;
}

struct safe_list*
safe_list_rbegin(struct safe_list *head)
{
  return safe_list_prev(head, head);
}

void
safe_list_riterate(struct safe_list **l, struct safe_list *head)
{
  struct safe_list *prev = safe_list_prev(*l, head);
  safe_list_relese(*l, head);
  *l = prev;
}

void
safe_list_move_to_end(struct safe_list *l, struct safe_list *head)
{
  if(l == head || l == head->prev)
    return;

  l->prev->next = l->next;
  l->next->prev = l->prev;

  l->next = head;
  l->prev = head->prev;
  head->prev->next = l;
  head->prev = l;
}

enum {
  DUMP2_OK     = 0,
  DUMP2_EPERM  = -1,  
  DUMP2_ENOPID = -2,  
  DUMP2_EINVAL = -3,  
  DUMP2_EFAULT = -4,  
};

struct cpu cpus[NCPU];

static struct safe_list proclist; //голова списка
static struct spinlock list_lock;
static int counter = 0;
static struct proc *first_zombie = 0; //первый зомби

struct proc *initproc;

int nextpid = 1;
struct spinlock pid_lock;

int fgpid = 0;
struct spinlock fgpid_lock;

extern void forkret(void);
static void freeproc(struct proc *p);

extern char trampoline[]; // trampoline.S

// helps ensure that wakeups of wait()ing
// parents are not lost. helps obey the
// memory model when using p->parent.
// must be acquired before any p->lock.
struct spinlock wait_lock;

// Allocate a page for each process's kernel stack.
// Map it high in memory, followed by an invalid
// guard page.
void
proc_mapstacks(pagetable_t kpgtbl)
{
  (void)kpgtbl;
}

// initialize the proc table.
void
procinit(void)
{
  initlock(&pid_lock, "nextpid");
  initlock(&wait_lock, "wait_lock");
  initlock(&list_lock, "list_lock");
  initlock(&fgpid_lock, "fgpid");
  fgpid = 0;
  safe_list_init(&proclist);
  counter = 0;
  first_zombie = 0;

#if defined(LOTTERY)
  for(int i = 0; i < NCPU; i++){
    cpus[i].randstate = 0x9e3779b97f4a7c15ULL ^ (uint64)(i + 1) * 0xbf58476d1ce4e5b9ULL;
    if(cpus[i].randstate == 0)
      cpus[i].randstate = 1;
  }
#endif
}

// Must be called with interrupts disabled,
// to prevent race with process being moved
// to a different CPU.
int
cpuid()
{
  int id = r_tp();
  return id;
}

// Return this CPU's cpu struct.
// Interrupts must be disabled.
struct cpu*
mycpu(void)
{
  int id = cpuid();
  struct cpu *c = &cpus[id];
  return c;
}

#if defined(LOTTERY)
static inline uint64
lottery_rand(struct cpu *c)
{
  uint64 x = c->randstate;
  if(x == 0)
    x = 1;
  x ^= x << 13;
  x ^= x >> 7;
  x ^= x << 17;
  c->randstate = x;
  return x;
}
#endif


// Return the current struct proc *, or zero if none.
struct proc*
myproc(void)
{
  push_off();
  struct cpu *c = mycpu();
  struct proc *p = c->proc;
  pop_off();
  return p;
}

int
allocpid()
{
  int pid;
  
  acquire(&pid_lock);
  pid = nextpid;
  nextpid = nextpid + 1;
  release(&pid_lock);

  return pid;
}

// Look in the process table for an UNUSED proc.
// If found, initialize state required to run in the kernel,
// and return with p->lock held.
// If there are no free procs, or a memory allocation fails, return 0.
static struct proc*
allocproc(void)
{
  struct proc *p = bd_malloc(sizeof(struct proc));
  if(p == 0)
    return 0;

  memset(p, 0, sizeof(*p));

  if((p->list = bd_malloc(sizeof(struct safe_list))) == 0){
    bd_free(p);
    return 0;
  }
  memset(p->list, 0, sizeof(*p->list));
  p->list->p = p;


  initlock(&p->lock, "proc");
  acquire(&p->lock);

  if((p->kstack = (uint64)kalloc()) == 0){
    freeproc(p);
    release(&p->lock);
    bd_free(p->list);
    bd_free(p);
    return 0;
  }

  p->pid = allocpid();
  p->state = USED;
  p->stopped = 0;
  p->isbg = 0;

  p->n_child = 0;

  p->tickets = 1;

  if((p->trapframe = (struct trapframe *)kalloc()) == 0){
    freeproc(p);
    release(&p->lock);
    bd_free(p->list);
    bd_free(p);
    return 0;
  }

  p->alarm_ticks  = 0;
  p->alarm_active = 0;
  p->cpu_used     = 0;
  p->cpu_limit    = 0;
  p->fsize_limit  = 0;

  p->sigpending      = 0;
  p->sigmask         = 0;
  p->handling_signal = 0;
  for(int i = 0; i < NSIG; i++){
    p->sighandlers[i]    = 0;
    p->sigdisposition[i] = SIG_DFL;
  }

  p->pagetable = proc_pagetable(p);
  if(p->pagetable == 0){
    freeproc(p);
    release(&p->lock);
    bd_free(p->list);
    bd_free(p);
    return 0;
  }

  memset(&p->context, 0, sizeof(p->context));
  p->context.ra = (uint64)forkret;
  p->context.sp = p->kstack + PGSIZE;

  return p;
}

// free a proc structure and the data hanging from it,
// including user pages.
// p->lock must be held.
static void
freeproc(struct proc *p)
{
  if(p->trapframe)
    kfree((void*)p->trapframe);
  p->trapframe = 0;
  if(p->pagetable)
    proc_freepagetable(p->pagetable, p->sz);
  p->pagetable = 0;
  if(p->kstack)
    kfree((void*)p->kstack);
  p->kstack = 0;

  p->sz = 0;
  p->pid = 0;
  p->parent = 0;
  p->name[0] = 0;
  p->n_child = 0;
  p->chan = 0;
  p->killed = 0;
  p->xstate = 0;
  p->stopped = 0;
  p->isbg = 0;

  p->sigpending      = 0;
  p->sigmask         = 0;
  p->handling_signal = 0;
  for(int i = 0; i < NSIG; i++){
    p->sighandlers[i]    = 0;
    p->sigdisposition[i] = SIG_DFL;
  }

  p->alarm_ticks  = 0;
  p->alarm_active = 0;

  p->cpu_used    = 0;
  p->cpu_limit   = 0;
  p->fsize_limit = 0;

  p->state = UNUSED;
}

// Create a user page table for a given process, with no user memory,
// but with trampoline and trapframe pages.
pagetable_t
proc_pagetable(struct proc *p)
{
  pagetable_t pagetable;

  // An empty page table.
  pagetable = uvmcreate();
  if(pagetable == 0)
    return 0;

  // map the trampoline code (for system call return)
  // at the highest user virtual address.
  // only the supervisor uses it, on the way
  // to/from user space, so not PTE_U.
  if(mappages(pagetable, TRAMPOLINE, PGSIZE,
              (uint64)trampoline, PTE_R | PTE_X) < 0){
    uvmfree(pagetable, 0);
    return 0;
  }

  // map the trapframe page just below the trampoline page, for
  // trampoline.S.
  if(mappages(pagetable, TRAPFRAME, PGSIZE,
              (uint64)(p->trapframe), PTE_R | PTE_W) < 0){
    uvmunmap(pagetable, TRAMPOLINE, 1, 0);
    uvmfree(pagetable, 0);
    return 0;
  }

  return pagetable;
}

// Free a process's page table, and free the
// physical memory it refers to.
void
proc_freepagetable(pagetable_t pagetable, uint64 sz)
{
  uvmunmap(pagetable, TRAMPOLINE, 1, 0);
  uvmunmap(pagetable, TRAPFRAME, 1, 0);
  uvmfree(pagetable, sz);
}

// a user program that calls exec("/init")
// assembled from ../user/initcode.S
// od -t xC ../user/initcode
uchar initcode[] = {
  0x17, 0x05, 0x00, 0x00, 0x13, 0x05, 0x45, 0x02,
  0x97, 0x05, 0x00, 0x00, 0x93, 0x85, 0x35, 0x02,
  0x93, 0x08, 0x70, 0x00, 0x73, 0x00, 0x00, 0x00,
  0x93, 0x08, 0x20, 0x00, 0x73, 0x00, 0x00, 0x00,
  0xef, 0xf0, 0x9f, 0xff, 0x2f, 0x69, 0x6e, 0x69,
  0x74, 0x00, 0x00, 0x24, 0x00, 0x00, 0x00, 0x00,
  0x00, 0x00, 0x00, 0x00
};

// Set up first user process.
void
userinit(void)
{
  struct proc *p;

  acquire(&list_lock);
  if(counter == NPROC)
    panic("No available procs for initproc");
  release(&list_lock);

  p = allocproc();
  if(p == 0)
    panic("userinit: allocproc");
  initproc = p;

  uvmfirst(p->pagetable, initcode, sizeof(initcode));
  p->sz = PGSIZE;

  p->trapframe->epc = 0;      
  p->trapframe->sp = PGSIZE;  

  safestrcpy(p->name, "initcode", sizeof(p->name));
  p->cwd = namei("/");

  p->state = RUNNABLE;
  release(&p->lock);

  acquire(&list_lock);
  counter++;
  if(first_zombie)
    safe_list_insert_before(p->list, first_zombie->list, &proclist);
  else
    safe_list_push_tail(p->list, &proclist);
  release(&list_lock);
}

// Grow or shrink user memory by n bytes.
// Return 0 on success, -1 on failure.
int
growproc(int n)
{
  uint64 sz;
  struct proc *p = myproc();

  sz = p->sz;
  if(n > 0){
    uint64 newsz = sz + (uint64)n;
    if(newsz < sz || newsz >= MAXVA)
      return -1;
    sz = newsz;
  } else if(n < 0){
    long long nn = (long long)n;
    long long ns = (long long)sz + nn;
    if(ns < 0)
      return -1;
    sz = uvmdealloc(p->pagetable, sz, (uint64)ns);
  }
  p->sz = sz;
  return 0;
}

// Create a new process, copying the parent.
// Sets up child kernel stack to return as if from fork() system call.
int
fork(void)
{
  int i, pid;
  struct proc *np;
  struct proc *p = myproc();

  acquire(&list_lock);
  if(counter == NPROC){
    release(&list_lock);
    return -1;
  }
  release(&list_lock);

  if((np = allocproc()) == 0){
    return -1;
  }

  if(uvmcopy(p->pagetable, np->pagetable, p->sz) < 0){
    freeproc(np);
    release(&np->lock);
    bd_free(np->list);
    bd_free(np);
    return -1;
  }
  np->sz = p->sz;

  *(np->trapframe) = *(p->trapframe);
  np->trapframe->a0 = 0;

  np->sigmask         = p->sigmask;
  np->sigpending      = 0;
  np->handling_signal = 0;
  np->stopped         = 0;
  for(i = 0; i < NSIG; i++){
    np->sighandlers[i]    = p->sighandlers[i];
    np->sigdisposition[i] = p->sigdisposition[i];
  }

  np->cpu_used     = 0;
  np->cpu_limit    = p->cpu_limit;
  np->fsize_limit  = p->fsize_limit;
  np->alarm_ticks  = 0;
  np->alarm_active = 0;

  np->tickets = p->tickets;

  for(i = 0; i < NOFILE; i++)
    if(p->ofile[i])
      np->ofile[i] = filedup(p->ofile[i]);
  np->cwd = idup(p->cwd);

  safestrcpy(np->name, p->name, sizeof(np->name));

  pid = np->pid;

  release(&np->lock);

  acquire(&wait_lock);
  np->parent = p;
  p->n_child++;
  release(&wait_lock);

  acquire(&np->lock);
  np->state = RUNNABLE;
  release(&np->lock);

  acquire(&list_lock);
  counter++;
  if(first_zombie)
    safe_list_insert_before(np->list, first_zombie->list, &proclist);
  else
    safe_list_push_tail(np->list, &proclist);
  release(&list_lock);

  return pid;
}

// Pass p's abandoned children to init.
// Caller must hold wait_lock.
void
reparent(struct proc *p)
{
  acquire(&list_lock);
  for(struct safe_list* pl = safe_list_begin(&proclist);
      pl != &proclist;
      safe_list_iterate(&pl, &proclist)){
    release(&list_lock);

    struct proc *pp = pl->p;

    if(pp->parent == p){
      if(pp->isbg == 0) {
        sigsend(pp->pid, SIGHUP);
      }
      pp->parent = initproc;
      initproc->n_child++;
      wakeup(initproc);
    }

    acquire(&list_lock);
  }
  release(&list_lock);
}

// Exit the current process.  Does not return.
// An exited process remains in the zombie state
// until its parent calls wait().
void
exit(int status)
{
  struct proc *p = myproc();

  if(p == initproc)
    panic("init exiting");

  for(int fd = 0; fd < NOFILE; fd++){
    if(p->ofile[fd]){
      struct file *f = p->ofile[fd];
      fileclose(f);
      p->ofile[fd] = 0;
    }
  }

  begin_op();
  iput(p->cwd);
  end_op();
  p->cwd = 0;

  if(p->pagetable){
    proc_freepagetable(p->pagetable, p->sz);
    p->pagetable = 0;
    p->sz = 0;
  }

  acquire(&wait_lock);

  reparent(p);

  if(p->parent){
    sigsend(p->parent->pid, SIGCHLD);
    wakeup(p->parent);
  }

  acquire(&list_lock);
  acquire(&p->lock);
  p->xstate = status;
  p->state = ZOMBIE;
  safe_list_move_to_end(p->list, &proclist);
  if(first_zombie == 0)
    first_zombie = p;
  release(&list_lock);

  release(&wait_lock);

  sched();
  panic("zombie exit");
}

// Wait for a child process to exit and return its pid.
// Return -1 if this process has no children.
int
wait(uint64 addr)
{
  struct proc *p = myproc();
  int pid;

  acquire(&wait_lock);
  for(;;){
    acquire(&list_lock);

    struct safe_list *sl = safe_list_rbegin(&proclist);
    while(sl != &proclist){
      struct proc *pp = sl->p;

      if(pp->state != ZOMBIE)
        break;

      if(pp->parent == p){
        acquire(&pp->lock);
        if(pp->state != ZOMBIE){
          release(&pp->lock);
          break;
        }

        pid = pp->pid;
        if(addr != 0 && copyout(p->pagetable, addr, (char *)&pp->xstate,
                                sizeof(pp->xstate)) < 0) {
          release(&pp->lock);
          safe_list_relese(sl, &proclist);
          release(&list_lock);
          release(&wait_lock);
          return -1;
        }

        if(pp == first_zombie){
          struct safe_list *n = pp->list->next;
          if(n == &proclist)
            first_zombie = 0;
          else
            first_zombie = n->p;
        }

        p->n_child--;

        freeproc(pp);
        release(&pp->lock);

        counter--;
        safe_list_remove(pp->list, &proclist);
        safe_list_relese(sl, &proclist);
        release(&list_lock);
        release(&wait_lock);
        return pid;
      }

      safe_list_riterate(&sl, &proclist);
    }

    safe_list_relese(sl, &proclist);
    release(&list_lock);

    if(p->n_child == 0 || killed(p)) {
      release(&wait_lock);
      return -1;
    }

    sleep(p, &wait_lock);
  }
}

// Per-CPU process scheduler.
// Each CPU calls scheduler() after setting itself up.
// Scheduler never returns.  It loops, doing:
//  - choose a process to run.
//  - swtch to start running that process.
//  - eventually that process transfers control
//    via swtch back to the scheduler.
void
scheduler(void)
{
  struct cpu *c = mycpu();

  c->proc = 0;
  for(;;){
    intr_on();

#if defined(LOTTERY)
    int total = 0;
    acquire(&list_lock);
    struct safe_list *pl = safe_list_begin(&proclist);
    while(pl != &proclist){
      if(first_zombie && pl == first_zombie->list)
        break;

      release(&list_lock);

      struct proc *p = pl->p;
      acquire(&p->lock);
      if(p->state == RUNNABLE && !p->stopped){
        int t = p->tickets;
        if(t < 1) t = 1;
        total += t;
      }
      release(&p->lock);

      acquire(&list_lock);
      safe_list_iterate(&pl, &proclist);
    }
    safe_list_relese(pl, &proclist);
    release(&list_lock);

    if(total == 0){
      asm volatile("wfi");
      continue;
    }

    uint64 draw = lottery_rand(c) % (uint64)total;

    int ran = 0;
    uint64 sum = 0;

    acquire(&list_lock);
    pl = safe_list_begin(&proclist);
    while(pl != &proclist){
      if(first_zombie && pl == first_zombie->list)
        break;

      release(&list_lock);

      struct proc *p = pl->p;
      acquire(&p->lock);
      if(p->state == RUNNABLE && !p->stopped){
        int t = p->tickets;
        if(t < 1) t = 1;
        sum += (uint64)t;
        if(sum > draw && !ran){
          p->state = RUNNING;
          c->proc = p;
          swtch(&c->context, &p->context);
          c->proc = 0;
          ran = 1;
        }
      }
      release(&p->lock);

      acquire(&list_lock);
      if(ran)
        break;
      safe_list_iterate(&pl, &proclist);
    }
    safe_list_relese(pl, &proclist);
    release(&list_lock);

    if(!ran)
      continue;

#elif defined(DEFAULT)
    int found = 0;
    acquire(&list_lock);
    struct safe_list* pl = safe_list_begin(&proclist);
    while(pl != &proclist){
      if(first_zombie && pl == first_zombie->list)
        break;

      release(&list_lock);

      struct proc *p = pl->p;

      acquire(&p->lock);
      if(p->state == RUNNABLE && !p->stopped) {
        p->state = RUNNING;
        c->proc = p;
        swtch(&c->context, &p->context);

        c->proc = 0;
        found = 1;
      }
      release(&p->lock);

      acquire(&list_lock);
      safe_list_iterate(&pl, &proclist);
    }
    safe_list_relese(pl, &proclist);
    release(&list_lock);

    if(found == 0)
      asm volatile("wfi");
#else
#error "Unknown scheduler policy. Use SCHEDPOLICY=DEFAULT or SCHEDPOLICY=LOTTERY"
#endif
  }
}

// Switch to scheduler.  Must hold only p->lock
// and have changed proc->state. Saves and restores
// intena because intena is a property of this
// kernel thread, not this CPU. It should
// be proc->intena and proc->noff, but that would
// break in the few places where a lock is held but
// there's no process.
void
sched(void)
{
  int intena;
  struct proc *p = myproc();

  if(!holding(&p->lock))
    panic("sched p->lock");
  if(mycpu()->noff != 1)
    panic("sched locks");
  if(p->state == RUNNING)
    panic("sched running");
  if(intr_get())
    panic("sched interruptible");

  intena = mycpu()->intena;
  swtch(&p->context, &mycpu()->context);
  mycpu()->intena = intena;
}

// Give up the CPU for one scheduling round.
void
yield(void)
{
  if (holding(&list_lock))
    panic("yield while holding global lock");
  struct proc *p = myproc();
  acquire(&p->lock);
  p->state = RUNNABLE;
  sched();
  release(&p->lock);
}

// A fork child's very first scheduling by scheduler()
// will swtch to forkret.
void
forkret(void)
{
  static int first = 1;

  release(&myproc()->lock);

  if (first) {
    fsinit(ROOTDEV);
    first = 0;
    __sync_synchronize();
  }

  usertrapret();
}

// Atomically release lock and sleep on chan.
// Reacquires lock when awakened.
void
sleep(void *chan, struct spinlock *lk)
{
  struct proc *p = myproc();

  acquire(&p->lock);
  release(lk);

  p->chan = chan;
  p->state = SLEEPING;
  sched();

  p->chan = 0;

  release(&p->lock);
  acquire(lk);
}

// Wake up all processes sleeping on chan.
// Must be called without any p->lock.
void
wakeup(void *chan)
{
  acquire(&list_lock);
  for(struct safe_list* pl = safe_list_begin(&proclist);
      pl != &proclist;
      safe_list_iterate(&pl, &proclist)){
    release(&list_lock);

    struct proc *p = pl->p;
    if(p != myproc()){
      acquire(&p->lock);
      if(p->state == SLEEPING && p->chan == chan) {
        p->state = RUNNABLE;
      }
      release(&p->lock);
    }

    acquire(&list_lock);
  }
  release(&list_lock);
}

void
proc_tick_alarms(void)
{
  acquire(&list_lock);
  for(struct safe_list *sl = proclist.next; sl != &proclist; sl = sl->next){
    struct proc *p = sl->p;
    if(p->state == UNUSED)
      continue;
    if(p->alarm_active && p->alarm_ticks > 0){
      p->alarm_ticks--;
      if(p->alarm_ticks == 0){
        // Mirror sigsend(pid, SIGALRM) side-effects without re-entering
        // the process list / locks from interrupt context.
        p->sigpending |= (1 << SIGALRM);
        p->alarm_active = 0;
      }
    }
  }
  release(&list_lock);
}

// Kill the process with the given pid.
// The victim won't exit until it tries to return
// to user space (see usertrap() in trap.c).
int
kill(int pid)
{
  acquire(&list_lock);
  for(struct safe_list* pl = safe_list_begin(&proclist);
      pl != &proclist;
      safe_list_iterate(&pl, &proclist)){
    release(&list_lock);

    struct proc *p = pl->p;
    acquire(&p->lock);
    if(p->pid == pid){
      p->killed = 1;
      if(p->state == SLEEPING){
        p->state = RUNNABLE;
      }
      release(&p->lock);

      acquire(&list_lock);
      safe_list_relese(pl, &proclist);
      release(&list_lock);
      return 0;
    }
    release(&p->lock);

    acquire(&list_lock);
  }
  release(&list_lock);
  return -1;
}

void
setkilled(struct proc *p)
{
  acquire(&p->lock);
  p->killed = 1;
  release(&p->lock);
}

int
killed(struct proc *p)
{
  int k;
  
  acquire(&p->lock);
  k = p->killed;
  release(&p->lock);
  return k;
}

// Copy to either a user address, or kernel address,
// depending on usr_dst.
// Returns 0 on success, -1 on error.
int
either_copyout(int user_dst, uint64 dst, void *src, uint64 len)
{
  struct proc *p = myproc();
  if(user_dst){
    return copyout(p->pagetable, dst, src, len);
  } else {
    memmove((char *)dst, src, len);
    return 0;
  }
}

// Copy from either a user address, or kernel address,
// depending on usr_src.
// Returns 0 on success, -1 on error.
int
either_copyin(void *dst, int user_src, uint64 src, uint64 len)
{
  struct proc *p = myproc();
  if(user_src){
    return copyin(p->pagetable, dst, src, len);
  } else {
    memmove(dst, (char*)src, len);
    return 0;
  }
}

// Print a process listing to console.  For debugging.
// Runs when user types ^P on console.
// No lock to avoid wedging a stuck machine further.
void
procdump(void)
{
  static char *states[] = {
  [UNUSED]    "unused",
  [USED]      "used",
  [SLEEPING]  "sleep ",
  [RUNNABLE]  "runble",
  [RUNNING]   "run   ",
  [ZOMBIE]    "zombie"
  };
  char *state;

  printf("\n");
  acquire(&list_lock);
  for(struct safe_list* sl = safe_list_begin(&proclist);
      sl != &proclist;
      safe_list_iterate(&sl, &proclist)) {
    release(&list_lock);

    struct proc* p = sl->p;
    if(p->state == UNUSED)
      goto next;
    if(p->state >= 0 && p->state < NELEM(states) && states[p->state])
      state = states[p->state];
    else
      state = "???";
    printf("%d %s %s\n", p->pid, state, p->name);

  next:
    acquire(&list_lock);
  }
  release(&list_lock);
}

void
proc_ps(void)
{
  static char *states[] = {
    [UNUSED]   "UNUSED",
    [USED]     "USED",
    [SLEEPING] "SLEEP",
    [RUNNABLE] "RUNNABLE",
    [RUNNING]  "RUN",
    [ZOMBIE]   "ZOMBIE",
  };

  printf("PID\tSTATE\t\tNAME\n");
  acquire(&list_lock);
  for(struct safe_list* sl = safe_list_begin(&proclist);
      sl != &proclist;
      safe_list_iterate(&sl, &proclist)) {
    release(&list_lock);
    struct proc *p = sl->p;

    acquire(&p->lock);
    if(p->state != UNUSED){
      char *st = "???";
      if(p->state >= 0 && p->state < NELEM(states) && states[p->state])
        st = states[p->state];
      printf("%d\t%s\t%s\n", p->pid, st, p->name);
    }
    release(&p->lock);

    acquire(&list_lock);
  }
  release(&list_lock);
}

void
dump(void)
{
  struct proc *p = myproc();
  struct trapframe *tf = p->trapframe;
  uint64 *regs[] = {
    &tf->s2, &tf->s3, &tf->s4, &tf->s5, &tf->s6,
    &tf->s7, &tf->s8, &tf->s9, &tf->s10, &tf->s11
  };

  for (int i = 0; i < 10; i++) {
    printf("s%d = %d\n", i + 2, (int)(*regs[i] & 0xFFFFFFFF));
  }
}

struct proc*
find_proc_by_pid(int pid)
{
  acquire(&list_lock);
  for(struct safe_list *sl = proclist.next; sl != &proclist; sl = sl->next){
    struct proc *p = sl->p;
    // pid never changes once assigned, so it is safe to check under list_lock.
    if(p->pid == pid){
      acquire(&p->lock);
      release(&list_lock);
      return p;
    }
  }
  release(&list_lock);
  return 0;
}

int
dump2(int pid, int regnum, uint64 uaddr)
{
  struct proc *caller = myproc();
  struct proc *target;

  if (pid == caller->pid) {
    target = caller;
    acquire(&target->lock);
  } else {
    target = find_proc_by_pid(pid); 
    if (target == 0)
      return DUMP2_ENOPID;
  }

  if (!(target == caller || target->parent == caller)) {
    release(&target->lock);
    return DUMP2_EPERM;
  }

  if (regnum < 2 || regnum > 11) {
    release(&target->lock);
    return DUMP2_EINVAL;
  }

  struct trapframe *tf = target->trapframe;
  uint64 *regs[] = {
    &tf->s2,  &tf->s3,  &tf->s4,  &tf->s5,  &tf->s6,
    &tf->s7,  &tf->s8,  &tf->s9,  &tf->s10, &tf->s11
  };
  uint64 val = *regs[regnum - 2];

  release(&target->lock);

  if (copyout(caller->pagetable, uaddr, (char*)&val, sizeof(val)) < 0)
    return DUMP2_EFAULT;

  return DUMP2_OK;
}

void
set_fgpid(int pid)
{
  acquire(&fgpid_lock);
  fgpid = pid;
  release(&fgpid_lock);
}

int
get_fgpid(void)
{
  int pid;
  acquire(&fgpid_lock);
  pid = fgpid;
  release(&fgpid_lock);
  return pid;
}