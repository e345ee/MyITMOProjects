package collectionStorageManager;

import java.io.IOException;
import java.util.ArrayList;

public interface FileManager<E> {

    ArrayList<E> readFromFile() throws IOException;

    public void writeToFile(ArrayList<E> productList, String value, Class<E> productClass);
}