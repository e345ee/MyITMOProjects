# I/O Library

Lab work 1: an input/output library in Assembly.

Implement a library of procedures that perform simple operations on strings,
numbers, and their textual representations.

# Preparation

* Read the first two chapters of "Low-level programming: C, assembly and
  program execution".

* Study the documentation for the following instructions and the Linux ABI
  documentation:

  - `jmp`, `ja`, and other conditional jump instructions
  - `cmp`
  - `test`
  - `xor`
  - `push` and `pop`
  - `call` and `ret`
  - `add`, `sub`, `mul`, `div`, `inc`, `dec`
  - `syscall`
  - `mov`, `lea`

  The documentation is huge. In a PDF viewer, find the document table of
  contents and look for volume two, "Instruction Set Reference", where each
  instruction has its own page.

* Read the documentation for the `read` system call with `man`. Its syscall
  number, placed in `rax`, is `0`. Information about the registers used to pass
  syscall parameters can be found in the syscall table from the course
  materials.

# Implementation

- Fill `lib.asm` with code instead of function stubs. Reuse already implemented
  functions where possible.
- Use `test.py` to test the implementation.

The `test.py` script generates a set of executable tests for each function, so
you can debug them individually. See also Appendix A in "Low-level programming:
C, assembly and program execution". During test execution, the code is checked
for calling convention compliance and 16-byte stack alignment before every
`call`.

# Common Mistakes

- A string of `n` bytes requires `n + 1` bytes because of the null terminator.
- Function labels must be global; the rest should be local.
- Registers do not contain zero by default.
- If you use callee-saved registers, you must preserve their values.
- If you use caller-saved registers, you must save their values before `call`
  and restore them afterward.
- Do not use buffers in the `.data` section. Allocate space on the stack by
  decreasing `rsp` instead.
- Functions receive arguments in `rdi`, `rsi`, `rdx`, `rcx`, `r8`, and `r9`.
- Do not print numbers character by character. Build a string in memory and
  call `print_string`.
- Check that `parse_int` and `parse_uint` set `rdx` correctly; this is very
  important for the next assignment.
- Check that `parse_int`, `parse_uint`, and `read_word` behave correctly when
  input ends with `Ctrl-D`.
- When using the stack, remember to decrease `rsp`.
- Before every `call`, align the stack to a multiple of 16.
- `syscall` may change the values of `rax`, `rcx`, and `r11`.
- Backticks allow C-style special characters such as `\n` and `\t`.

The solution code is usually around 250 lines.
