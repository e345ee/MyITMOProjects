package commandManager.commands;

import logger.ServerLogger;
import models.comparators.PriceComparator;
import models.handlers.OrganizationIDHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import products.Product;
import responses.CommandStatusResponse;

import java.util.Collections;
import java.util.Comparator;

public class AddIfMinCommand implements BaseCommand, ArgumentConsumer<Product> {

    private CommandStatusResponse response;
    private Product obj;

    @Override
    public String getName() {
        return "add_if_min";
    }

    @Override
    public String getDescr() {
        return "Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции.";
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



        Comparator<Product> comparator = new PriceComparator();

        if (productHandler.getCollection().isEmpty()) {
            response = CommandStatusResponse.ofString("Коллекция пуста.");
        } else {

            Product mimProduct = Collections.min(productHandler.getCollection(), comparator);
            if (comparator.compare(obj, mimProduct) < 0) {
                productHandler.addElementToCollection(obj);
                response = CommandStatusResponse.ofString("Объект добавлен в коллекцию.");
            } else {
                response = CommandStatusResponse.ofString("Объект не добавлен.");
            }
        }




        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }

    @Override
    public void setObj(Product obj) {
        this.obj = obj;
        obj.setId(ProductIDHandler.generateId());
        obj.getManufacturer().setId(OrganizationIDHandler.generateId());
    }
}