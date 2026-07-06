%ifndef COLON_MACRO
%define COLON_MACRO

%define adress 0

%macro colon 2
    %ifid %2
        %2:
        dq adress
    %else
        %error "Неверный аргумент."
    %endif

    %ifstr %1
        db %1, 0
    %else
        %error "Неверный аргумент."
    %endif

    %define adress %2
%endmacro

%endif 