package commandManager.commands;

import logger.ServerLogger;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import products.Organization;
import products.Product;
import responses.CommandStatusResponse;

public class RemoveAllByManufacturerCommand implements BaseCommand, ArgumentConsumer<Organization> {

    private CommandStatusResponse response;

    private Organization obj;

    @Override
    public void setObj(Organization obj) {
        this.obj = obj;

        obj.setId(OrganizationIDHandler.generateId());
    }

    @Override
    public String getName() {
        return "remove_all_by_manufacturer";
    }

    @Override
    public String getDescr() {
        return "Удаляет из коллекции все элементы, значение поля manufacturer которого эквивалентно заданному.";
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

        StringBuilder log = new StringBuilder();

        boolean flag = productHandler.getCollection().removeIf(product -> {
            if (product.getManufacturer().equals(obj)) {
                log.append(String.format("\nОбъект id:%d удалён.\n", product.getId()));
                productHandler.clearUniqueFieldByProduct(product);
                return true;  // Удалить продукт
            }
            return false;  // Не удалять продукт
        });

        if (!flag) {
            log.append("Равных мануфактур не найдено. Ничего не удалено.");
        }

// Преобразуем StringBuilder в строку, если нужно где-то использовать результат
        String resultLog = log.toString();

        response = CommandStatusResponse.ofString(resultLog);


        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }

}
