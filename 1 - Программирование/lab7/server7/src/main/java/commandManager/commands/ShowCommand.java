package commandManager.commands;

import client.ClientHandler;
import collectionStorageManager.PostgresSQLManager;
import models.comporators.PriceComparator;
import models.comporators.ProductComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import models.handlers.ProductHandler;
import products.Product;
import responses.CommandStatusResponse;

import java.util.ArrayList;
import java.util.List;

public class ShowCommand implements BaseCommand {
    private static final Logger logger = LogManager.getLogger("ShowCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescr() {
        return "Выводит все объекты в коллекции.";
    }

    @Override
    public void execute(String[] args) {
        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();
        PostgresSQLManager postgresSQLManager = new PostgresSQLManager();

        List<Product> productsList = new ArrayList<>(postgresSQLManager.getCollectionFromDatabase());
        productHandler.addMissingCitiesToCollection(productsList);

        StringBuilder sb = new StringBuilder();

        if (productHandler.getCollection().isEmpty()) {
            response = CommandStatusResponse.ofString("Нечего показывать.");
        } else {
            final int[] index = {1};  // Индекс для нумерации

            productHandler.getCollection().stream().sorted(new ProductComparator()).forEachOrdered(e -> {
                sb.append(index[0]).append(") ").append(e.toString()).append("\n\n");
                index[0]++;
            });

            response = CommandStatusResponse.ofString(sb.toString());
        }

        logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }

    @Override
    public void setClient(ClientHandler clientManager) {
        this.client = clientManager;
    }
}