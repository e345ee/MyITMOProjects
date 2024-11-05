package exceptions;


/**
 * Исключение для обработки ввода неизвестной команды.
 */
public class UnknownCommandException extends Exception{


    /**
     * Конструктор с сообщением.
     *
     * @param message Сообщения об ошибке.
     */

    public UnknownCommandException(String message){
        super(message);
    }
}
