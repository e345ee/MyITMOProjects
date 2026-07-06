package commandManager.commands;


import client.ClientHandler;
import collectionStorageManager.PostgresSQLManager;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import products.Product;
import responses.CommandStatusResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class UpdateCommand implements BaseCommand, ArgumentConsumer<Product> {
    private static final Logger logger = LogManager.getLogger("UpdateCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

    private Product obj;

    @Override
    public void setObj(Product obj) {
        this.obj = obj;
        obj.setId(ProductIDHandler.generateId());
        obj.getManufacturer().setId(OrganizationIDHandler.generateId());
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescr() {
        return "Обновляет значения элемента в коллекции по id.";
    }

    @Override
    public String getArgs() {
        return "id {element}";
    }

    @Override
    public void execute(String[] args) {

        if (args.length>2){
            throw new IllegalArgumentException("Команда " + getName()+ " имеет только один аргумент.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();
        int finalId = Integer.parseInt(args[1]);

        PostgresSQLManager dbManager = new PostgresSQLManager();

        if (!dbManager.isProductOwnedByUser(finalId, client)) {
            response = new CommandStatusResponse("У вас нет доступа на модификацию этого элемента или его нет.", 2);
            logger.warn(response.getResponse());
            return;
        }

        PartNumberHandler.releasePN(obj.getPartNumber());

        for (Product product : productHandler.getCollection()){
            if (product.getId() == finalId){
                obj.getManufacturer().setId(product.getManufacturer().getId());
            }
        }
        obj.setId(finalId); // Set ID before updating city in the database
        boolean updated = dbManager.updateProduct(obj); // Updating city in the database

        if (updated) {
            // Update the city in the collection
            productHandler.getCollection().removeIf(product -> Objects.equals(product.getId(), finalId));
            productHandler.addElementToCollection(obj);

            response = CommandStatusResponse.ofString("Объект добавлен!\n Его ID: " + finalId);
        } else {
            PartNumberHandler.addPN(obj.getPartNumber());
            response = new CommandStatusResponse("Ошибка добавления объекта.", 2);
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