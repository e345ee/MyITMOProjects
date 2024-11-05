package commandManager.commands;

import logger.ServerLogger;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import products.Product;
import responses.CommandStatusResponse;

public class AddCommand implements BaseCommand, ArgumentConsumer<Product> {

    private CommandStatusResponse response;

    private Product obj;

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
        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();

        boolean hasPN = PartNumberHandler.checkPN(obj.getPartNumber());

        if (hasPN) {
            response = CommandStatusResponse.ofString("Объект не добавлен. partNumber не уникален.");
        } else {
            productHandler.addElementToCollection(obj);
            response = CommandStatusResponse.ofString("Элемент добавлен!");
        }


        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}