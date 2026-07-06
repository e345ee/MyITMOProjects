# Memory Allocator

This assignment is about implementing a custom memory allocator in C.

The allocator manages a heap made of memory regions obtained with `mmap`.
Inside those regions, memory is represented as a linked sequence of blocks. Each
block has a header and a payload area returned to the user.

# First Approximation

Every block has a header similar to:

```c
struct mem {
  struct mem* next;
  size_t capacity;
  bool is_free;
  uint8_t contents[];
};
```

The `contents` field marks the beginning of the memory area returned to the
user. The allocator must be able to move from a user pointer back to the block
header by subtracting `sizeof(struct mem)`.

## Basic `malloc(n)` Idea

- Traverse the heap blocks.
- Find a free block with enough capacity.
- Mark it as occupied.
- Return a pointer to `contents`.

If the free block is larger than requested, it may be split into an occupied
part and a smaller free block.

## Basic `free(void* addr)` Idea

To free a block, obtain the block header from `addr` and set `is_free = true`.

# Second Approximation

The full allocator must also handle several practical questions:

- What should be done with many consecutive free blocks?
- How can blocks that are too small be avoided?
- What should happen when the heap runs out of memory?
- How should heap memory be released at allocator shutdown?

## `malloc(n)` Algorithm

- There is no point in allocating a block with a capacity such as 1 byte,
  because even the block header is larger. Define a minimum block capacity:

```c
#define BLOCK_MIN_CAPACITY 24
```

- Too-small blocks may appear in two cases:
  - `n < BLOCK_MIN_CAPACITY`; request a block of `BLOCK_MIN_CAPACITY` instead
    of `n`.
  - A suitable block is found, but it is only slightly larger than `n`. Splitting
    it would create a second block with capacity smaller than
    `BLOCK_MIN_CAPACITY`; in that case, do not split the block and return it
    whole.

- While searching for a suitable block, traverse heap blocks. Before deciding
  whether a block is suitable, merge it with all following free blocks.

- If the heap has no memory left, extend it with the `mmap` system call. Read
  `man mmap` carefully to understand which `prot` and `flags` arguments are
  needed.

  - First, try to allocate memory directly after the current heap end and mark
    it as one large free block. If the last block in the previous heap region
    was free, merge it with the new block.
  - If allocating a contiguous region fails, allocate a region wherever the OS
    can place it. Link the last block of the previous region to the first block
    of the new region.

## `free(void* addr)` Algorithm

In addition to marking the block as free, merge it with all following free
blocks when possible.

## `heap_term(void)` Algorithm

To shut down the allocator, traverse all memory regions previously allocated
with `mmap` and release them with `munmap`.

# Assignment

- Implement the allocator using the repository template.
- Design tests that demonstrate allocator behavior in important cases:
  - ordinary successful memory allocation;
  - freeing one block among several allocated blocks;
  - freeing two blocks among several allocated blocks;
  - memory runs out and a new memory region extends the old one;
  - memory runs out, the old region cannot be extended because another address
    range is already allocated, and a new region is allocated elsewhere.

  Tests must run from `main.c`, but they may be described in a separate file or
  files. The algorithm is not trivial and it is easy to make mistakes, so split
  the implementation into small functions.

- Understand how the `Makefile` works and fix it so the final code includes the
  compiled `main.c`. You may write your own `Makefile` if it is cleaner and
  more expressive.

# Self-Check

- Read the C style guide from the course materials. Your solution must follow
  it.
- Check the architecture.
- Please submit the solution as a pull request. As a fallback, a repository link
  on GitLab or GitHub is acceptable.

# Additional Materials

- [Doug Lea's article on how the allocator in `glibc` works](http://gee.cs.oswego.edu/dl/html/malloc.html). The current allocator version uses a more complex algorithm.
- [Source code of one of the recent `glibc` allocator versions with many well-written comments](docs/malloc-impl.c).
