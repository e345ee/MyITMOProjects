# Dictionary

Lab work 2: a dictionary in Assembly.

# Preparation

* Read chapters 3, 4, and 5 of "Low-level programming: C, assembly and program
  execution".

During the defense, we may discuss the compilation cycle, the role of the
linker and preprocessor, virtual memory organization, and the relationship
between sections, segments, and memory regions. We may also discuss protection
rings and privileged mode.

## Linked List

A linked list is a data structure. An empty list is a null pointer; a non-empty
list is a pointer to the first list element. Each element contains data and a
pointer to the next element.

For example, a linked list `(100, 200, 300)` may start at the pointer `x1`:

```text
x1 -> [100 | *-] -> [200 | *-] -> [300 | 0]
```

Containers store sets of data and provide operations such as accessing
elements, inserting elements at the beginning, at the end, or at an arbitrary
position, and sorting.

Different containers make some operations easy and fast while making others
slow. For example, arrays are inconvenient for insertion, but existing elements
can be accessed quickly by index. Linked lists make insertion convenient, but
index-based access is harder because the list must be traversed from the start.

## Assignment

Implement an Assembly dictionary as a linked list.

Each entry contains the address of the next key-value pair, a key, and a value.
Keys and values are addresses of null-terminated strings.

The dictionary is defined statically, and each new element is added to its
beginning. Macros automate this process: when a new element is declared through
a new language construct, it is automatically added to the beginning of the
list and the pointer to the list head is updated. This removes the need to
manually maintain list links.

Create a `colon` macro with two arguments: a key and the label associated with
the value. The label cannot be generated from the value itself because the
string may contain characters that cannot appear in labels, such as arithmetic
symbols or punctuation. After using the macro, the value associated with the key
can be specified directly.

Example:

```nasm
colon "hello", hello_value
db "world", 0
```

The implementation must provide these files:

- `lib.asm` - input/output library from the first assignment
- `lib.inc` - header file for the input/output library
- `colon.inc` - header file with the `colon` macro definition
- `dict.asm` - implementation of the `find_word` dictionary lookup function
- `dict.inc` - header file for the dictionary lookup function
- `words.inc` - dictionary definition used in tests
- `main.asm` - simplest console interface for dictionary lookup

### Notes

- Package the functions implemented in the first lab as a separate `lib.o`
  library.

  Remember to make all function names global labels and list them in `lib.inc`.

- To avoid copying files between repositories, you may add the first lab
  repository as a Git submodule.

- By default, CI runners do not download submodule contents; that requires an
  additional command.

- Create `colon.inc` and define a macro for creating dictionary words.

  The macro accepts two parameters:
  - key, in quotes
  - label name used to find the value

- Create the `find_word` function in `dict.asm` and `dict.inc`. It accepts two
  arguments:
  - pointer to a null-terminated string
  - pointer to the beginning of the dictionary

  `find_word` traverses the entire dictionary looking for a matching key. If a
  matching entry is found, it returns the address of the beginning of the
  dictionary entry, not the value. Otherwise, it returns `0`.

- `words.inc` must store words defined through the `colon` macro. Include this
  file in `main.asm`.

- Define `_start` in `main.asm`. It should:
  - read a string of no more than 255 characters from `stdin` into a buffer;
  - try to find an entry in the dictionary;
  - print the value for the key to `stdout` if found;
  - print an error message otherwise.

  Remember that error messages must be printed to `stderr`.

- Provide a `Makefile` that tracks file dependencies correctly. The default
  target must build the assignment.
- Write tests for the dictionary implementation. Tests must run through the
  `test` target in the Makefile.
