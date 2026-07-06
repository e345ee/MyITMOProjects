package exceptions;

/**
 * Исключение для обработки ошибки в построении объекта.
 */
public class BuildObjectException extends Exception{

    /**
     * Конструктор по умолчанию.
     */
    public BuildObjectException(){
        super("Building object goes wrong.");
    }

    /**
     * Конструктор с сообщением.
     *
     * @param message Сообщения об ошибке.
     */
    public BuildObjectException(String message){
        super(message);
    }
}
