package commandManager.commands;

import logger.ServerLogger;
import models.handlers.ProductHandler;
import responses.CommandStatusResponse;

public class SaveCommand implements BaseCommand {

    private CommandStatusResponse response;

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescr() {
        return "Сохраняет. Только для сервера.";
    }

    @Override
    public void execute(String[] args) {
        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        ServerLogger.logger.trace("Сохранение...");
        ProductHandler writer = ProductHandler.getInstance();
        response = CommandStatusResponse.ofString("[Server] Коллекция сохранена...\nРазмер коллекции: " + writer.getCollection().size());
        ServerLogger.logger.info(response.getResponse());
        writer.writeCollection();
        response = CommandStatusResponse.ofString("[Server] Сохранение завершено.\nРазмер коллекции: " + writer.getCollection().size());
        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}