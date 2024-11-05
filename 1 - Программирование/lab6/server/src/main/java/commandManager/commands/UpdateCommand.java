package commandManager.commands;

import logger.ServerLogger;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import products.Product;
import responses.CommandStatusResponse;

public class UpdateCommand implements BaseCommand, ArgumentConsumer<Product> {

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

        int productId = Integer.parseInt(args[1]);

        // Проверка наличия продукта с данным ID
        ProductHandler handler = ProductHandler.getInstance();
        Product oldProduct = handler.getCollection().stream()
                .filter(product -> product.getId() == productId)
                .findFirst()
                .orElse(null);

        if (oldProduct == null) {
            // Если продукт с таким ID не найден
            response = CommandStatusResponse.ofString("Такого элемента с таким id нет в коллекции.");
        } else {
            // Очищаем уникальные поля старого продукта
            handler.clearUniqueFieldByProduct(oldProduct);

            // Устанавливаем ID новому продукту
            obj.setId(productId);

            // Удаляем старый продукт и добавляем новый
            handler.getCollection().remove(oldProduct);
            handler.addElementToCollection(obj);

            response = CommandStatusResponse.ofString("Элемент обновлен");
        }

        // Логирование результата
        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}