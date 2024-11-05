package commandManager.commands;


import client.ClientHandler;
import models.handlers.ProductHandler;
import responses.CommandStatusResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveCommand implements BaseCommand {
    private static final Logger logger = LogManager.getLogger("SaveCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

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

       logger.trace("Сохранение...");
        ProductHandler writer = ProductHandler.getInstance();
        response = CommandStatusResponse.ofString("[Server] Коллекция сохранена...\nРазмер коллекции: " + writer.getCollection().size());
        logger.info(response.getResponse());
       // writer.writeCollectionToDatabase();
        response = CommandStatusResponse.ofString("[Server] Сохранение завершено.\nРазмер коллекции: " + writer.getCollection().size());
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