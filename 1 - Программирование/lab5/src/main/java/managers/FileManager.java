package managers;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Интерфейс для создания Менеджера способного читать из файла
 * @param <E> Объекты этого типа будет читаться из файла.
 */
public interface FileManager<E> {

    ArrayList<E> readFromFile() throws IOException;

    public void writeToFile(ArrayList<E> productList, String value, Class<E> productClass);
}
