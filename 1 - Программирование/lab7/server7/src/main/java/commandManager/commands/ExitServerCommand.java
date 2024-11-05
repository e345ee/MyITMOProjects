package commandManager.commands;

import client.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import responses.CommandStatusResponse;

public class ExitServerCommand implements BaseCommand {
    private static final Logger logger = LogManager.getLogger("ExitServerCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

    @Override
    public String getName() {
        return "exit_server";
    }

    @Override
    public String getDescr() {
        return "Вырубает сервер, заранее сохраняя.";
    }

    @Override
    public void execute(String[] args) {
        new SaveCommand().execute(new String[0]);
        response = CommandStatusResponse.ofString("Сервер отключен.");
       logger.info(response.getResponse());

        System.exit(0);
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