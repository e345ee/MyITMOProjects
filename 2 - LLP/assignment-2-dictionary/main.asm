%include "lib.inc"
%include "dict.inc"
%include "words.inc"

%define BUFFER_SIZE 256

%define STDOUT 1
%define STDERR 2
%define WRITE_SYSCALL 1
%define READ_SYSCALL 0

section .bss
    buffer: resb BUFFER_SIZE

section .rodata
    prompt_msg: db "Введите ключ для поиска в словаре:", 10, 0
    overflow_msg: db "Ошибка: Ввод превышает 255 символов.", 10, 0
    not_found_msg: db "Ошибка: Ключ не найден в словаре.", 10, 0

section .text

start:
    mov rdi, prompt_msg
    call print_string

    mov rdi, buffer
    mov rsi, BUFFER_SIZE
    call read_line
    ;mov rdi, rax
    ;call print_string
    test rax, rax
    jz .overflow_error      
    test rdx, rdx              
    mov rdi, rax
    mov rsi, spbgu     
    call find_word
    test rax, rax
    je .not_found_error

    mov r12, rax
    mov rdi, rax
    add rdi, 8
    call string_length

    mov rdi, r12
    add rdi, 8
    add rdi, rax
    inc rdi
    call print_string
    call print_newline
    jmp .exit_program

.empty_input:
    mov rdi, not_found_msg
    call print_error
    jmp .exit_program

.overflow_error:
    mov rdi, overflow_msg
    call print_error
    jmp .exit_program

.not_found_error:
    mov rdi, not_found_msg
    call print_error

.exit_program:
    xor rdi, rdi
    call exit

print_error:
    push rdi
    call string_length
    mov rdx, rax
    pop rsi
    mov rax, WRITE_SYSCALL
    mov rdi, STDERR
    syscall
    mov rdi, 1
    call exit