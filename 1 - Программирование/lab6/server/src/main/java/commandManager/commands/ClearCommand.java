package commandManager.commands;

import logger.ServerLogger;
import models.handlers.ProductHandler;
import responses.CommandStatusResponse;

public class ClearCommand implements BaseCommand {

    private CommandStatusResponse response;

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescr() {
        return "Очищает коллекцию.";
    }

    @Override
    public void execute(String[] args) {

        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();

        productHandler.getCollection().clear();
        productHandler.clearUniqueFields();

        response = CommandStatusResponse.ofString("Очищено!");
        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}