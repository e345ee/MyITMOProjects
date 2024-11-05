package collectionStorageManager;

import exceptions.PathNotFoundException;

/**
 * Класс для извлечения пути к файлу из строки.
 */
public class FilePathExtractor implements PathExtractable {

    /**
     * Метод возвращает валидный путь до файла.
     * @param args строка, из которой нужно извлечь файл.
     * @return Возвращает валидный путь.
     * @throws PathNotFoundException Если путь не валиден.
     */
    public String getPath(String[] args) throws PathNotFoundException {
        String path;

        if (args.length != 1) {
            throw new PathNotFoundException("Ошибка: аргументы командной строки заданы неверно");
        }

        path = args[0];

        return path;
    }
}
