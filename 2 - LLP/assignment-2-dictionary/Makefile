ASM = nasm
ASMFLAGS = -f elf64
TMP_DIR = tmp
RM = rm -rf
PYTHON = python3

all: build

$(TMP_DIR):
	mkdir -p $(TMP_DIR)

$(TMP_DIR)/%.o: %.asm | $(TMP_DIR)
	$(ASM) $(ASMFLAGS) -o $@ $<

$(TMP_DIR)/main.o: $(TMP_DIR)/lib.o $(TMP_DIR)/dict.o main.asm words.inc
	$(ASM) $(ASMFLAGS) -o $@ main.asm

build: $(TMP_DIR)/main.o $(TMP_DIR)/lib.o $(TMP_DIR)/dict.o
	ld -o main $(TMP_DIR)/main.o $(TMP_DIR)/lib.o $(TMP_DIR)/dict.o

.PHONY: all clean rebuild test

clean:
	$(RM) $(TMP_DIR)/
	$(RM) main

rebuild: clean build

test: rebuild
	$(PYTHON) test.py

