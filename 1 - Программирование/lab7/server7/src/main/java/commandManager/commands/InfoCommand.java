package commandManager.commands;
import client.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import models.handlers.ProductHandler;
import responses.CommandStatusResponse;

public class InfoCommand implements BaseCommand {
    private static final Logger logger = LogManager.getLogger("InfoCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

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
        logger.info(response.getResponse());
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
