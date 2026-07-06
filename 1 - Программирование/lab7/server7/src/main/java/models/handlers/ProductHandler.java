package models.handlers;

import client.ClientHandler;
import collectionStorageManager.PostgresSQLManager;
import products.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import responses.CommandStatusResponse;

public class ProductHandler implements CollectionHandler<ArrayList<Product>, Product> {

    private static final Logger logger = LogManager.getLogger("ProductHandler");
    private final ReentrantLock lock = new ReentrantLock(true);

    private CommandStatusResponse response;

    ClientHandler clientHandler;

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    private static ProductHandler singletonPattern;
    private LocalDate initDate = null;


    private ArrayList<Product> products;


    private ProductHandler() {
    }


    public static ProductHandler getInstance() {
        if (singletonPattern == null)
            singletonPattern = new ProductHandler();
        return singletonPattern;
    }


    @Override
    public void loadCollectionFromDatabase() {
        PostgresSQLManager dbManager = new PostgresSQLManager();
        ArrayList<Product> productsDB = dbManager.getCollectionFromDatabase();
        ProductHandler productHandler = ProductHandler.getInstance();
        for (Product product : productsDB) {
            productHandler.addElementToCollection(product);
        }
        if (productsDB.isEmpty()) {
            setCollection(new ArrayList<>());
        }
        if (!(products == null) && !getCollection().isEmpty()) {
            initDate = Objects.requireNonNull(getCollection().stream().min(Comparator.comparing(Product::getCreationDate)).orElse(null)).getCreationDate();
        } else {
            initDate = null;
        }
    }

    /**
     * @Override
     *     public void writeCollectionToDatabase() {
     *         lock.lock();
     *         try {
     *             PostgresSQLManager dbManager = new PostgresSQLManager();
     *             dbManager.writeCollectionToDatabase();
     *         } finally {
     *             lock.unlock();
     *         }
     *     }
     */






    @Override
    public ArrayList<Product> getCollection() {
        lock.lock();
        try {
            return products;
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void setCollection(ArrayList<Product> products) {
        lock.lock();
        try {
            this.products = products;
            for (Product product : products) {
                PartNumberHandler.addPN(product.getPartNumber());
            }
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void addElementToCollection(Product product) {
        lock.lock();
        try {
            if (products != null)
                products.add(product);
            else {

                ArrayList<Product> products1 = new ArrayList<>();
                products1.add(product);
                ProductHandler.getInstance().setCollection(products1);
            }
            PartNumberHandler.addPN(product.getPartNumber());
        } finally {
            lock.unlock();
        }
    }


    @Override
    @Deprecated
    public void clearCollection() {
        products.clear();
        PartNumberHandler.clearPN();
    }


    @Override
    public Product getFirstOrNew() {
        lock.lock();
        try {
            if (products.iterator().hasNext())
                return products.iterator().next();
            else
                return new Product();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public LocalDate getInitDate() {
        lock.lock();
        try {
            return initDate;
        } finally {
            lock.unlock();
        }
    }


    public void addMissingCitiesToCollection(List<Product> productsFromDatabase) {

            PartNumberHandler.clearPN();
            setCollection((ArrayList<Product>) productsFromDatabase);

    }


    public CommandStatusResponse getResponse() {
        return response;
    }
}
