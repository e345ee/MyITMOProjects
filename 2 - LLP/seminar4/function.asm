section .data
    input_buffer db 0, 0, 0, 0, 0 , 0
    result dq 0
    scale dq 1000.0
    neg_half dq -0.5
    three dq 3.0
    six dq 6.0
    nine dq 9.0
    neg_flag db 0

section .bss
    temp resq 1

section .text
global _start
extern read_line
extern parse_int
extern print_uint
extern print_int
extern print_char
extern exit

_start:
    mov rdi, input_buffer
    mov rsi, 5
    call read_line
    mov rdi, input_buffer
    call parse_int
    
    
    cmp rax, -3000
    jl first_function
    cmp rax, 0
    jl second_function
    cmp rax, 6000
    jl third_function
    jmp forth_function


third_function:
    mov [result], rax
    fild qword [result]
    fld qword [scale]
    fdivp st1, st0
    fld qword [neg_half]
    fmulp st1, st0
    fld qword [three]
    faddp st1, st0
    fld qword [scale]
    fmulp st1, st0
    fistp qword [temp]
    mov rax, [temp]
    test rax, rax
    jge print_result
    mov byte [neg_flag], 1
    neg rax
    mov [temp], rax
    mov rdi, '-'
    call print_char
    jmp print_result

forth_function:
    mov [result], rax
    fild qword [result]
    fld qword [scale]
    fdivp st1, st0
    fld qword [six]
    fsubp st1, st0
    fld qword [scale]
    fmulp st1, st0
    fistp qword [temp]
    mov rax, [temp]
    test rax, rax
    jge print_result
    mov byte [neg_flag], 1
    neg rax
    mov [temp], rax
    mov rdi, '-'
    call print_char
    jmp print_result

first_function:
    mov [result], rax
    fild qword [result]
    fld qword [scale]
    fdivp st1, st0
    fld qword [three]
    faddp st1, st0
    fld qword [scale]
    fmulp st1, st0
    fistp qword [temp]
    mov rax, [temp]
    test rax, rax
    jge print_result
    mov byte [neg_flag], 1
    neg rax
    mov [temp], rax
    mov rdi, '-'
    call print_char
    jmp print_result

second_function:
    mov [result], rax
    fild qword [result]
    fld qword [scale]
    fdivp st1, st0
    fmul st0, st0
    fld qword [nine]
    fsub st0, st1
    fsqrt
    fld qword [scale]
    fmulp st1, st0
    fistp qword [temp]
    mov rax, [temp]
    test rax, rax
    jge print_result
    mov byte [neg_flag], 1
    neg rax
    mov [temp], rax
    mov rdi, '-'
    call print_char
    jmp print_result

print_result:
    mov rdi, [temp]
    call print_uint
    mov rdi, 0
    call exit
