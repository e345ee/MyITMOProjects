# Common Mistakes

This file lists typical mistakes made in the image transformation assignment.

## Architecture

- Do not place all code in `main.c`. Split the solution into modules for file
  I/O, BMP parsing/writing, image representation, transformations, and utility
  functions.
- Keep the internal image representation independent from BMP. The program
  should be able to support other formats later without rewriting the rotation
  logic.
- Avoid global mutable state. Pass data through function arguments and return
  values.
- Make ownership clear: the code that allocates memory must also have a clear
  path for freeing it.

## Error Handling

- Check every file operation.
- Check every memory allocation.
- Return meaningful error codes when execution cannot continue.
- Treat non-critical cleanup problems, such as a failed `fclose`, as warnings
  rather than fatal errors.
- Do not ignore invalid input files or unsupported BMP formats.

## BMP Format

- Remember that BMP rows are padded to a multiple of 4 bytes.
- Do not assume that the file stores pixels in the same order as your internal
  image structure.
- This assignment only requires support for 24-bit BMP files.
- Validate header fields before reading pixel data.
- Preserve required header values when writing the result.

## Memory

- Do not read arbitrary amounts of data into fixed-size buffers.
- Free every allocated image buffer on all error paths.
- Be careful with integer overflow when calculating image sizes and row
  padding.
- Avoid returning pointers to stack-allocated objects.

## Transformations

- Rotation changes image dimensions.
- Keep coordinate conversion logic explicit and test it on small images.
- Avoid modifying the source image in place unless the algorithm is designed for
  it.

## Build and Tests

- Keep the provided CMake structure working.
- Run the tester before submitting.
- Build with sanitizers when possible.
- Run `clang-tidy` if it is available in your environment.
