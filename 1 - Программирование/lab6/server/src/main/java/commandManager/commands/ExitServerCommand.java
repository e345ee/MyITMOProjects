package commandManager.commands;

import logger.ServerLogger;
import models.handlers.ProductHandler;
import responses.CommandStatusResponse;

public class ExitServerCommand implements BaseCommand {

    private CommandStatusResponse response;

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
        ServerLogger.logger.info(response.getResponse());

        System.exit(0);
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}