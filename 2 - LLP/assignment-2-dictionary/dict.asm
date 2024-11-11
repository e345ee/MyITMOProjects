%include "lib.inc"

section .text
global find_word
find_word:
    push r8
    push r9
    mov r8, rdi
    mov r9, rsi
    jmp .start_loop

.next_element:
    mov r9, [r9]
    test r9, r9
    jz .fail

.start_loop:

    test r9, r9
    je .fail
    mov rax, r9
    add rax, 8
    mov rdi, rax
    mov rsi, r8
    call string_equals
    test rax, rax
    jz .next_element

.success:
    mov rax, r9
    jmp .end

.fail:
    xor rax, rax

.end:
    pop r9
    pop r8
    ret