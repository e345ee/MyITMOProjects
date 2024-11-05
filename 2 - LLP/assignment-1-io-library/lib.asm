%macro syscall_exit 1
    mov rdi, %1
    mov rax, 60        
    syscall             
%endmacro

%macro syscall_rw 4
    mov rax, %1       
    mov rdi, %2        
    mov rsi, %3        
    mov rdx, %4        
    syscall
%endmacro

%macro syscall_write 3
    syscall_rw 1, %1, %2, %3  
%endmacro

%macro save_regs 0
    mov [rbp - 8], rdi
    mov [rbp - 16], rsi
    mov [rbp - 24], rdx
%endmacro

%macro restore_regs 0
    mov rdi, [rbp - 8]
    mov rsi, [rbp - 16]
    mov rdx, [rbp - 24]
%endmacro

%macro check_digit 0
    cmp cl, '0'               
    jb .done                  
    cmp cl, '9'               
    ja .done                  
%endmacro


section .text

; Принимает код возврата и завершает текущий процесс
exit:
    syscall_exit rdi      

string_length:
    xor rax, rax        
.loop:
    cmp byte [rdi + rax], 0  
    je .done            
    inc rax             
    jmp .loop           
.done:
    ret                 

print_string:
    push rdi            
    call string_length  
    mov rdx, rax        
    pop rsi            
    syscall_write 1, rsi, rdx 
    ret        


print_newline:
    mov rdi, '/n'

print_char:
    push rdi            
    mov rsi, rsp       
    mov rdx, 1  
    syscall_write 1, rsi, rdx 
    add rsp, 8            
    ret     



; Выводит беззнаковое 8-байтовое число в десятичном формате 
; Совет: выделите место в стеке и храните там результаты деления
; Не забудьте перевести цифры в их ASCII коды.
print_uint:
    push rbx
    sub rsp, 20
    mov rbx, 10
    mov rax, rdi
    lea rsi, [rsp + 20]
    test rax, rax
    jne .division
    mov byte [rsp], '0'
    mov rdx, 1
    mov rsi, rsp
    sub rsp, 8
    syscall_write 1, rsi, rdx
    add rsp, 8
    jmp .end
.division:
    xor rdx, rdx
    div rbx
    add dl, '0'
    dec rsi
    mov [rsi], dl
    test rax, rax
    jne .division
    mov rdx, rsp
    add rdx, 20
    sub rdx, rsi
    syscall_write 1, rsi, rdx
.end:
    add rsp, 20
    pop rbx
    ret

; Выводит знаковое 8-байтовое число в десятичном формате 
print_int:
    push rdi                 
    test rdi, rdi                 
    jge .positive_number       
    mov rdi, '-'                     
    call print_char           
    pop rdi
    neg rdi
    jmp .pointer_skip_pop_for_negativ                   
.positive_number:
    pop rdi
.pointer_skip_pop_for_negativ:
    jmp print_uint 

; Принимает два указателя на нуль-терминированные строки, возвращает 1 если они равны, 0 иначе
string_equals:
    push rbx                           
.loop:
    mov al, [rdi]           
    mov bl, [rsi]           
    xor al, bl              
    jne .unequal          
    test bl, bl             
    jz .equal              
    inc rdi                 
    inc rsi                 
    jmp .loop               
.equal:
    mov rax, 1              
    jmp .done                    
.unequal:
    xor rax, rax            
.done:
    pop rbx
    ret

; Читает один символ из stdin и возвращает его. Возвращает 0 если достигнут конец потока
read_char:
    xor rax,rax
    push rax 
    syscall_rw 0, 0, rsp, 1            
    pop rax              
    ret   

; Принимает: адрес начала буфера, размер буфера
; Читает в буфер слово из stdin, пропуская пробельные символы в начале, .
; Пробельные символы это пробел 0x20, табуляция 0x9 и перевод строки 0xA.
; Останавливается и возвращает 0 если слово слишком большое для буфера
; При успехе возвращает адрес буфера в rax, длину слова в rdx.
; При неудаче возвращает 0 в rax
; Эта функция должна дописывать к слову нуль-терминатор
read_word:
    push rbp
    mov rbp, rsp
    sub rsp, 32
.skip_blank:
    save_regs               
    call read_char
    restore_regs              
    cmp rax, ' '
    je .skip_blank
    cmp rax, `\t`
    je .skip_blank
    cmp rax, `\n`
    je .done
.read:
    cmp rax, 0
    je .done
    cmp rax, ' '
    je .done
    cmp rdx, rsi
    jae .overflow_error
    mov [rdi + rdx], al
    inc rdx
    save_regs                 
    call read_char
    restore_regs              
    jmp .read
.overflow_error:
    xor rdx, rdx
    xor rax, rax
    leave
    ret
.done:
    mov byte [rdi + rdx], 0
    mov rax, rdi
    leave
    ret

; Принимает указатель на строку, пытается
; прочитать из её начала беззнаковое число.
; Возвращает в rax: число, rdx : его длину в символах
; rdx = 0 если число прочитать не удалось
parse_uint:
    push rbx                  
    xor rax, rax              
    xor rsi, rsi              
    mov r8, 10                
.parse:
    movzx rcx, byte [rdi + rsi] 
    cmp rcx, 0                
    je .done         
    check_digit       
    mov rbx, rax              
    imul rbx, r8              
    mov rax, rbx              
    sub cl, '0'               
    add rax, rcx              
    inc rsi                   
    jmp .parse          
.done:
    mov rdx, rsi              
    pop rbx                   
    ret

; Принимает указатель на строку, пытается
; прочитать из её начала знаковое число.
; Если есть знак, пробелы между ним и числом не разрешены.
; Возвращает в rax: число, rdx : его длину в символах (включая знак, если он был) 
; rdx = 0 если число прочитать не удалось
parse_int:
    cmp byte [rdi], '-'          
    jne parse_uint              
    inc rdi                      
    sub rsp, 8                  
    call parse_uint              
    add rsp, 8                   
    neg rax                      
    inc rdx                     
    ret                          
   

; Принимает указатель на строку, указатель на буфер и длину буфера
; Копирует строку в буфер
; Возвращает длину строки если она умещается в буфер, иначе 0
string_copy:
    push rbx                 
    push rdi
    push rsi
    push rdx
    
    sub rsp, 8
    call string_length
    add rsp, 8

    pop rdx
    pop rsi
    pop rdi   
.check_buffer:
    cmp rax, rdx              
    jae .overflow_error       
    mov rcx, rax              
    xor rax, rax              
.copy_loop:
    cmp rax, rcx              
    je .add_terminator    
    mov bl, [rdi + rax]       
    mov [rsi + rax], bl       
    inc rax                   
    jmp .copy_loop            
.add_terminator:
    mov byte [rsi + rax], 0   
    jmp .done
.overflow_error:
    xor rax, rax              
    pop rbx                   
    ret
.done:
    pop rbx                   
    ret
