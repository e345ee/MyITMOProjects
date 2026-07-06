ORG 0x0
V0: WORD $default, 0X180
V1: WORD $int1,      0X180
V2: WORD $int2, 0X180
V3: WORD $default,      0x180
V4: WORD $default, 0X180
V5: WORD $default, 0X180
V6: WORD $default, 0X180
V7: WORD $default, 0X180
 
ORG 0x04B
X: WORD 0X456
 
max_val: WORD 0x001A   ; 26, максимальное значение Х
min_val: WORD 0xFFE7   ; -25 , минимальное значение Х
default:    IRET   ; Обработка прерывания по умолчанию
 
 
START:   DI
      CLA
OUT 0x1
OUT 0x7
OUT 0xB
OUT 0xD
OUT 0x11
OUT 0x15
OUT 0x19
OUT 0x1D
  LD #0x9 ; Загрузка в аккумулятор MR (1000|0001=1001)
      OUT 3  ; Разрешение прерываний для 1 ВУ
      LD #0xA   ; Загрузка в аккумулятор MR (1000|0010=1010)
      OUT 5  ; Разрешение прерываний для 2 ВУ
      EI
 
main:    DI   ; Запрет прерываний чтобы обеспечить атом. операции
     LD X
      DEC
     CALL check
      ST X
      EI
      JUMP main
 
int1:   ; Обработка прерывания на ВУ-1
 
    LD X
  ;HLT
      ASL
  ASL
  ADD X
  NEG
      ADD #2
      OUT 2
 ; HLT
      IRET
 
int2:  ; Обработка прерывания на ВУ-2
      ;HLT
      IN 4
      ADD X
CALL check
      ST X
       ;HLT
      IRET
 
check:                ; Проверка принадлежности X к ОДЗ
check_min: CMP min_val  ; Если x > min переход на проверку верхней границы
      BPL check_max   
     JUMP ld_max  ; Иначе загрузка max в аккумулятор
check_max:  CMP max_val ; Проверка пересечения верхней границы X
      BMI return              ; Если x < max переход
ld_max: LD max_val    ; Загрузка максимальное значения в X  
return: RET                ; Метка возврата из проверки на ОДЗ  
