// Physical memory allocator, for user processes,
// kernel stacks, page-table pages,
// and pipe buffers. Allocates whole 4096-byte pages.

#include "types.h"
#include "param.h"
#include "memlayout.h"
#include "spinlock.h"
#include "riscv.h"
#include "defs.h"

void freerange(void *pa_start, void *pa_end);

extern char end[]; // first address after kernel.
                   // defined by kernel.ld.

struct run {
  struct run *next;
};

struct {
  struct spinlock lock;
  struct run *freelist;
} kmem;

#define NPAGES ((PHYSTOP - KERNBASE) / PGSIZE)

static struct {
  struct spinlock lock;
  int refcnt[NPAGES];
} kmemref;

static inline int
pa2idx(uint64 pa)
{
  if(pa < KERNBASE || pa >= PHYSTOP)
    panic("pa2idx");
  return (pa - KERNBASE) / PGSIZE;
}

void
kref_inc(uint64 pa)
{
  acquire(&kmemref.lock);
  kmemref.refcnt[pa2idx(pa)]++;
  release(&kmemref.lock);
}

int
kref_get(uint64 pa)
{
  int r;
  acquire(&kmemref.lock);
  r = kmemref.refcnt[pa2idx(pa)];
  release(&kmemref.lock);
  return r;
}

int
kref_dec(uint64 pa)
{
  int r;
  acquire(&kmemref.lock);
  int idx = pa2idx(pa);
  if(kmemref.refcnt[idx] < 1)
    panic("kref_dec");
  kmemref.refcnt[idx]--;
  r = kmemref.refcnt[idx];
  release(&kmemref.lock);
  return r;
}

void
kinit()
{
  initlock(&kmem.lock, "kmem");

  initlock(&kmemref.lock, "kmemref");
  memset(kmemref.refcnt, 0, sizeof(kmemref.refcnt));

  bd_init((void*)PGROUNDUP((uint64)end), (void*)PHYSTOP);
}

void
freerange(void *pa_start, void *pa_end)
{
  char *p;
  p = (char*)PGROUNDUP((uint64)pa_start);
  for(; p + PGSIZE <= (char*)pa_end; p += PGSIZE)
    kfree(p);
}

// Free the page of physical memory pointed at by pa,
// which normally should have been returned by a
// call to kalloc().  (The exception is when
// initializing the allocator; see kinit above.)
void
kfree(void *pa)
{
  if((uint64)pa % PGSIZE != 0 || (uint64)pa < (uint64)end || (uint64)pa >= PHYSTOP)
    panic("kfree");

  if(kref_dec((uint64)pa) > 0)
    return;

  memset(pa, 1, PGSIZE);

  bd_free(pa);
}

// Allocate one 4096-byte page of physical memory.
// Returns a pointer that the kernel can use.
// Returns 0 if the memory cannot be allocated.
void*
kalloc(void)
{
  void *p;

  p = bd_malloc(PGSIZE);
  if(p){
    acquire(&kmemref.lock);
    kmemref.refcnt[pa2idx((uint64)p)] = 1;
    release(&kmemref.lock);
    memset(p, 5, PGSIZE);
  }

  return p;
}
