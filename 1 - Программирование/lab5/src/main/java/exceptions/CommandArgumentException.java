package exceptions;

/**
 * Исключение для обработки ввода некорректного аргумента команды.
 */
public class CommandArgumentException extends Exception{

    /**
     * Конструктор с сообщением.
     *
     * @param message Сообщения об ошибке.
     */
    public CommandArgumentException(String message){
        super(message);
    }
}
