package commandManager.commands;

import logger.ServerLogger;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import products.Product;
import responses.CommandStatusResponse;

import java.util.Objects;

public class RemoveByIdCommand implements BaseCommand {

    private CommandStatusResponse response;

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescr() {
        return "Удаляет элемент по значению id.";
    }

    @Override
    public String getArgs() {
        return "id";
    }

    @Override
    public void execute(String[] args) {
        if (args.length>2){
            throw new IllegalArgumentException("Команда " + getName()+ " имеет только один аргумент.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();
        Integer productId = Integer.valueOf(args[1]);

        // Получаем продукт по id
        Product product = productHandler.getElementById(productId);

        if (product != null && productHandler.getCollection().removeIf(p -> Objects.equals(p.getId(), productId))) {
            // Освобождаем уникальные поля продукта, только если он существует
            ProductIDHandler.releaseId(product.getId());
            OrganizationIDHandler.releaseId(product.getManufacturer().getId());
            PartNumberHandler.releasePN(product.getPartNumber());

            response = CommandStatusResponse.ofString("Объект с id: " + args[1] + " удален.");
        } else {
            // Продукт с таким id не найден
            response = CommandStatusResponse.ofString("Элемента с таким id нет в коллекции.");
        }

        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}