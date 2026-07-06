# Development with Visual Studio

## 1. Select and clone your GitLab fork

Clone your fork of the assignment repository and open it in Visual Studio.

## 2. In the solution window, select `Folder View` in the left panel

Visual Studio will run the CMake project configuration with the default profile
(**`x64-Debug`**).

## 3. Select the required configuration from the dropdown

- **`x64-Debug`** builds quickly and is suitable for development.
- **`x64-Asan`** is useful for debugging segmentation faults and other memory
  problems. It is recommended to run your code with ASan before submitting it
  for review.
- **`x64-Release`** is used to build optimized code and check performance.

## 4. Use the top panels to build and test

- Build options are located in the **`Build`** menu. Press **`F7`** to build the
  whole solution.
- To run tests, select **`Run CTests for ...`** in the **`Test`** menu.
