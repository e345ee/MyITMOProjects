package commandManager.commands;

import client.ClientHandler;
import collectionStorageManager.PostgresSQLManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import products.Product;
import responses.CommandStatusResponse;

import java.util.ArrayList;

public class AddCommand implements BaseCommand, ArgumentConsumer<Product> {
    private static final Logger logger = LogManager.getLogger("AddCommand");

    private CommandStatusResponse response;

    private Product obj;
    private ClientHandler client;

    @Override
    public void setObj(Product obj) {
        this.obj = obj;
        obj.setId(ProductIDHandler.generateId());
        obj.getManufacturer().setId(OrganizationIDHandler.generateId());
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescr() {
        return "Добавляет элемент в коллекцию.";
    }

    @Override
    public String getArgs() {
        return "{element}";
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            throw new IllegalArgumentException("Команда " + getName() + " не имеет аргументов.");
        }

        boolean hasPN = PartNumberHandler.checkPN(obj.getPartNumber());
        PostgresSQLManager manager = new PostgresSQLManager();

        // Add the new element to the database and retrieve its generated ID
        ArrayList<Integer> ids = manager.writeObjectToDatabase(obj, client);
        int generatedId =  ids.get(1);

        if (generatedId != -1 && !hasPN && ids.get(0) != -1) {
            // Set the generated ID for the new element
            obj.setId(generatedId);
            obj.getManufacturer().setId(Long.valueOf(ids.get(0)));
            ProductHandler collectionHandler = ProductHandler.getInstance();
            collectionHandler.addElementToCollection(obj);

            response = CommandStatusResponse.ofString("Элемент добавлен с ID: " + generatedId);
        } else {
            response = CommandStatusResponse.ofString("Ошибка добавления элемента.");
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