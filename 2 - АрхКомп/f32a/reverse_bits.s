.data
input_addr:      .word 0x80
output_addr:     .word 0x84
n:               .word 0
result:          .word 0
counter:         .word 0
inv_flag:        .word 0

.text
.org 0x100

_start:
    load_and_prepare
    rev_loop
    apply_sign
    write_result
    halt


load_and_prepare:
    @p input_addr a! @
    dup
    !p n
    lit 1 and
    !p inv_flag

    lit 0
    !p result

    lit 32
    !p counter
    ;


rev_loop:
    @p counter
    dup
    if end_rev

    lit -1 +
    !p counter

    @p result
    2*
    @p n
    lit 1 and
    +
    !p result

    @p n
    2/
    !p n

    rev_loop ;
end_rev:
    ;


apply_sign:
    @p inv_flag
    lit 1 and
    -if end_sign

    @p result
    inv
    lit 1 +
    !p result
end_sign:
    ;


write_result:
    @p output_addr a!
    @p result
    !
    ;
