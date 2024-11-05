package managers;

import exceptions.PathNotFoundException;

/**
 * Интерфейс для извлечения пуи до файла из строки.
 */
public interface PathExtractable {

    /**
     * Возвращает путь до файла.
     * @param args строка, из которой нужно извлечь файл.
     * @return Строка с путем на точно существующий файло.
     * @throws PathNotFoundException если путь на файл задан некорректно и его невозможно извлечь из строки.
     */
    String getPath(String[] args) throws PathNotFoundException;
}
