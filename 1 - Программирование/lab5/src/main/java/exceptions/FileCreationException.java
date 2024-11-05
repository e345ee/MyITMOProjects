package exceptions;

import java.nio.file.FileSystemException;


/**
 * Исключение для обработки неудачного создания файла.
 */
public class FileCreationException extends FileSystemException {

    /**
     * Конструктор по умолчанию.
     */
    public FileCreationException() {
        super("Failed to create file.");
    }

    /**
     * Конструктор с сообщением.
     *
     * @param message Сообщения об ошибке.
     */
    public FileCreationException(String message) {
        super(message);
    }
}