# Working with the Terminal

## 1. Configuration

```bash
cmake -B build -DCMAKE_BUILD_TYPE=<config>
```

Instead of `<config>`, use one of the existing configurations:

- **`Debug`** builds quickly and is suitable for development.
- **`ASan, LSan, MSan, UBSan`** are useful for debugging segmentation faults and
  other memory problems. It is recommended to run your code with sanitizers
  before submitting it for review.
- **`Release`** is used to build optimized code and check performance.

## 2. Build

```bash
cmake --build build
```

The executable files `./build/solution/image-transformer` and
`./build/tester/image-matcher` will be built and can be used for manual testing.

## 3. Testing

```bash
ctest --test-dir build
```

Or:

```bash
cmake --build build --target test
```

## Bonus: `ssh` + `git`

### How to Set Up SSH Keys

```bash
ssh-keygen
cat ~/.ssh/id_rsa.pub
```

Open the `SSH Keys` category in your GitLab profile settings, add a new key,
and paste the contents of `id_rsa.pub` there.

### How to Clone a Fork

```bash
git clone git@gitlab.se.ifmo.ru:<your-user>/<repo>.git
```

### How to Push Your Changes Back to the Fork

```bash
git add .
git commit -m "Implement solution"
git push
```

After you open a merge request, every new change pushed this way will appear
there automatically.

### How to Update Your Lab When the Teacher Asks You to Pull Fresh Changes

```bash
git remote add upstream <main-repository-url>
git fetch upstream
git merge upstream/master
```
