TARGET = function
LIB = lib.o
SRC = function.asm
PYTHON = python3

ASM = nasm
LD = ld

ASM_FLAGS = -f elf64

all: build

build: $(TARGET)

$(LIB): lib.asm
	$(ASM) $(ASM_FLAGS) -o $@ $^

$(TARGET): $(SRC) $(LIB)
	$(ASM) $(ASM_FLAGS) -o main.o $(SRC)
	$(LD)  -o $(TARGET) main.o $(LIB)

clean:
	rm -f $(TARGET) main.o $(LIB)

rebuild: clean build

test:
	make rebuild
	 $(PYTHON) test.py
