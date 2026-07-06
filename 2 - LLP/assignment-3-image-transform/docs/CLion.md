# Development with CLion

## 1. Select and clone your GitLab fork

Clone your fork of the assignment repository and open it in CLion.

## 2. In the project window, select `CMakeLists.txt` in the left panel

At the top of the editor window, CLion should show a prompt asking to load the
CMake project. Click **`Load CMake project`**.

The build window may show an error such as `Unexpected build type MSan, possible
values: Debug;Release;ASan;LSan;UBSan`. This means that the configuration is not
available on your OS or with your compiler, but it will not prevent development
with other configurations.

## 3. Select the required configuration from the dropdown

- **`Debug`** builds quickly and is suitable for development.
- **`ASan, LSan, MSan, UBSan`** are useful for debugging segmentation faults and
  other memory problems. It is recommended to run your code with sanitizers
  before submitting it for review.
- **`Release`** is used to build optimized code and check performance.

Select **`All CTest`** as the build target. You can now build and run the
project using the buttons in the top-right corner as usual.

If you get an error such as `C:\CLion 2022.2.4\bin\mingw\bin/ld.exe: cannot
find -lasan`, the required library is missing for that profile. Select another
configuration.
