package exceptions;

import java.io.IOException;

/**
 * Исключение для обработки неправильного ввода пути до json файла.
 */
public class PathNotFoundException extends IOException {

    /**
     * Конструктор по умолчанию.
     */
    PathNotFoundException(){
        super("Failed to get path to a file.");
    }

    /**
     * Конструктор с сообщением.
     *
     * @param message Сообщения об ошибке.
     */
    public PathNotFoundException(String message){
        super(message);
    }
}
