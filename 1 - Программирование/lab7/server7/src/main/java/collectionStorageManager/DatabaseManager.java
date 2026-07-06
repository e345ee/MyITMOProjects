package collectionStorageManager;

import client.ClientHandler;
import products.Product;

import java.util.ArrayList;

public interface DatabaseManager {
    ArrayList<Product> getCollectionFromDatabase();

    void writeCollectionToDatabase( ClientHandler clientHandler);
}
