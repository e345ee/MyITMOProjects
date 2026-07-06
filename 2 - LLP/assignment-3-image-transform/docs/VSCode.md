# Development with Visual Studio Code

Development requires these extensions:

- **`CMake`** by `twxs`
- **`CMakeTools`** by `Microsoft`
- **`C/C++`** by `Microsoft`

Reload VS Code after installing the extensions.

## 1. Select and clone your GitLab fork

Clone your fork of the assignment repository and open the project folder in VS
Code.

## 2. Select the compiler in the project window

VS Code should ask you to select a compiler when the project is opened. If it
does not, click the wrench button on the bottom panel.

On Windows, you most likely need the `Visual Studio` compiler with the `amd64`
variant.

## 3. Select the configuration on the bottom panel

- **`Debug`** builds quickly and is suitable for development.
- **`ASan, LSan, MSan, UBSan`** are useful for debugging segmentation faults and
  other memory problems. It is recommended to run your code with sanitizers
  before submitting it for review.
- **`Release`** is used to build optimized code and check performance.

Use **`Build`** on the same bottom panel to build the code and **`Run CTests
tests`** to run tests.

If you see an error such as `...\Microsoft.CppBuild.targets(457,5): error
MSB8013: This project doesn't contain the Configuration and Platform combination
of MSan|x64`, the selected configuration is not supported on your system. Choose
another one.
