package commandManager.commands;

import logger.ServerLogger;
import models.handlers.ProductHandler;
import responses.CommandStatusResponse;

public class InfoCommand implements BaseCommand {

    private CommandStatusResponse response;

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescr() {
        return "Выводит информацию о коллекции.";
    }

    @Override
    public void execute(String[] args) {
        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();



        String sb = "Тип коллекции: " + productHandler.getCollection().getClass().getName() + ", заполнена элементами типа: " + productHandler.getFirstOrNew().getClass().getName() + '\n' +
                "Размер коллекции: " + productHandler.getCollection().size() + '\n' +
                "Дата инициализации: " + productHandler.getInitDate();

        response = CommandStatusResponse.ofString(sb);
        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}