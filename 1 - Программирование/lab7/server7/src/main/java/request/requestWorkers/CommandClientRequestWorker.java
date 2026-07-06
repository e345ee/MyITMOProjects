package request.requestWorkers;

import client.ClientHandler;
import commandManager.CommandManager;
import exceptions.UserNotAuthenticatedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.requests.ServerRequest;
import requests.CommandClientRequest;

public class CommandClientRequestWorker implements RequestWorker {
    private static final Logger logger = LogManager.getLogger("CommandClientRequestWorker");

    @Override
    public void workWithRequest(ServerRequest request) {
        try {
            CommandClientRequest requestToWork = (CommandClientRequest) request.getUserRequest();

            ClientHandler clientManager = new ClientHandler(requestToWork.getName(), requestToWork.getPasswd());
            clientManager.authUserCommand();

            CommandManager manager = new CommandManager();
            manager.executeCommand(requestToWork, request.getFrom(), request.getConnection() , clientManager);
        } catch (UserNotAuthenticatedException e) {
            logger.error("Что-то пошло не так во время аутентификации: " + e.getMessage());
        }
    }
}