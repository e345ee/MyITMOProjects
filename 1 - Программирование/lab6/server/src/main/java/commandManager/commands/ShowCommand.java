package commandManager.commands;

import logger.ServerLogger;
import models.handlers.ProductHandler;
import responses.CommandStatusResponse;

public class ShowCommand implements BaseCommand {

    private CommandStatusResponse response;

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescr() {
        return "Выводит все объекты в коллекции.";
    }

    @Override
    public void execute(String[] args) {
        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();

        StringBuilder sb = new StringBuilder();

        if (productHandler.getCollection().isEmpty()) {
            response = CommandStatusResponse.ofString("Нечего показывать.");
        } else {
            final int[] index = {1};  // Индекс для нумерации

            productHandler.getCollection().forEach(e -> {
                sb.append(index[0]).append(") ").append(e.toString()).append('\n');
                index[0]++;
            });

            response = CommandStatusResponse.ofString(sb.toString());
        }

        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}