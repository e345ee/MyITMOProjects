package commandManager.commands;

import client.ClientHandler;
import logger.ServerLogger;
import responses.CommandStatusResponse;

public class ExitCommand implements BaseCommand {

    private CommandStatusResponse response;

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescr() {
        return "Выключает клиент.";
    }

    @Override
    public void execute(String[] args) {
        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        ServerLogger.logger.trace("Получена команда выхода от клиента");
        ServerLogger.logger.info("Сохранение коллекции");
        ClientHandler.getInstance().allowNewCallerBack();
        SaveCommand saveCommand = new SaveCommand();
        saveCommand.execute(new String[0]);
        response = CommandStatusResponse.ofString("Коллекция сохранена. Вы готовы к выходу!");
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}
