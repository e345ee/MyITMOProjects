package commandManager.commands;


import client.ClientHandler;
import responses.CommandStatusResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExitCommand implements BaseCommand {
    private static final Logger logger = LogManager.getLogger("ExitCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

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

        logger.trace("Получена команда выхода от клиента");
        logger.info("Сохранение коллекции");
        SaveCommand saveCommand = new SaveCommand();
        saveCommand.execute(new String[0]);
        response = CommandStatusResponse.ofString("Коллекция сохранена. Вы готовы к выходу!");
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }

    @Override
    public void setClient(ClientHandler clientManager) {
        this.client = clientManager;
    }
}
