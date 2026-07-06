# Image Transformer

This lab is about implementing a C program that reads a BMP image, applies a
geometric transformation, and writes the transformed image back to disk.

The assignment focuses on modular design, binary file parsing, memory
management, error handling, and testing.

## Task

Implement an image transformation utility with this interface:

```bash
./image-transformer <source-image> <transformed-image>
```

The program must:

1. Open the source BMP file.
2. Read and validate its headers.
3. Convert the BMP data into an internal image representation.
4. Rotate the image.
5. Save the transformed image as a BMP file.
6. Correctly release all allocated resources.

Only 24-bit BMP files are required for this assignment.

## Suggested Architecture

The solution should be split into meaningful modules. A reasonable structure is:

- `image` - internal image representation and allocation/freeing logic.
- `bmp` - reading and writing BMP files.
- `rotation` - image rotation logic.
- `file_utils` - opening, closing, and validating files.
- `main` - argument parsing and high-level control flow.

The BMP format must not leak into transformation logic. The rotation code should
work with the internal image representation, not directly with BMP headers.

## BMP Notes

BMP rows are padded to a multiple of 4 bytes. This padding is stored in the file
but should not be treated as image pixels.

When reading a BMP file:

- validate the signature and required header fields;
- check that the image is 24-bit;
- calculate row padding correctly;
- allocate enough memory for the internal image;
- handle file and allocation errors.

When writing a BMP file:

- create valid BMP headers;
- write rows with correct padding;
- report errors if writing fails.

## Rotation

The transformation changes pixel coordinates and may change image dimensions.
Keep coordinate conversion explicit and test it on small images where the
expected result is easy to verify.

## Build and Test System

The repository provides a CMake-based build system. You do not need to write a
build system from scratch.

Depending on the platform and compiler, several configurations with dynamic
analysis tools may be available:

- `Debug` - fast build for development.
- `ASan` - detects invalid memory access such as use-after-free, double-free,
  stack/heap/static buffer overflows.
- `LSan` - detects memory leaks.
- `MSan` - checks that memory is initialized before it is read.
- `UBSan` - detects common undefined behavior, such as integer overflow.
- `Release` - optimized build for performance checks.

If `clang-tidy` is installed, it may run during compilation. The check list is
defined in `clang-tidy-checks.txt`; you may add your own checks to the end of
that file.

The `tester` directory contains testing code and images. Tests are run through
CTest.

The build system is integrated with CLion, Visual Studio, and Visual Studio
Code.

## Requirements

### Linux and macOS

- A compiler (`gcc` or `clang`) and `cmake` version 3.12 or newer.
- If you want to use sanitizers with GCC, install `libasan`, `liblsan`, and
  `libubsan` through your package manager. Package names may differ.
- If you want to use sanitizers with Clang, some systems require the
  `compiler-rt` package.
- If you want to use `clang-tidy`, install `clang-tools-extra`.

### Windows

- An IDE such as CLion, Visual Studio, or Visual Studio Code.
- If you want to use `clang-tidy`, download LLVM from
  https://github.com/llvm/llvm-project/releases and choose a win64 installer.
- VS Code additionally requires Visual Studio and CMake.

## Build and Test Instructions

- [Working with the terminal](docs/Terminal.md)
- [Development with CLion](docs/CLion.md)
- [Development with Visual Studio](docs/Visual%20Studio.md)
- [Development with Visual Studio Code](docs/VSCode.md)

# Error Handling

If an error occurs during program execution, handle it and terminate with the
appropriate error code when continuing is impossible. Standard error codes can
be found in `/usr/include/asm-generic/errno-base.h`; for example, if memory is
not available, return `ENOMEM`, which equals `12`.

If an error is not critical, such as failing to close a file after all data was
written, treat it as a warning and continue where possible.

# Self-Check

- Read the C style guide from the course materials. Your solution must follow
  it.
- Think about architecture: how would you organize the code so it is easy to add
  new input formats, not only BMP, and new transformations, not only rotation?
- Please submit the solution as a pull request.
