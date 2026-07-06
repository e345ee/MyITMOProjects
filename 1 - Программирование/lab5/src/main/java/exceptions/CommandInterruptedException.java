package exceptions;

/**
 * Исключение для обработки прерывания исполняемой команды.
 */
public class CommandInterruptedException extends RuntimeException{

    /**
     * Конструктор по умолчанию.
     */
    public CommandInterruptedException(Exception cause)
    {
        super(cause);
    }

    /**
     * Конструктор с сообщением.
     *
     * @param message Сообщения об ошибке.
     */
    public CommandInterruptedException(String message){super(message);}
}
