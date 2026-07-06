package request.requestWorkers;

import client.ClientHandler;
import commandManager.commands.ArgumentConsumer;
import exceptions.UserNotAuthenticatedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.requests.ServerRequest;
import requests.ArgumentCommandClientRequest;
import commandManager.CommandManager;

public class ArgumentCommandClientRequestWorker<T, Y> implements RequestWorker {
    private static final Logger logger = LogManager.getLogger("ArgumentCommandClientRequestWorker");

    @Override
    public void workWithRequest(ServerRequest request) {
        try {
            ArgumentCommandClientRequest<T> requestToWork = (ArgumentCommandClientRequest<T>) request.getUserRequest();

            ClientHandler clientManager = new ClientHandler(requestToWork.getName(), requestToWork.getPasswd());
            clientManager.authUserCommand();

            CommandManager manager = new CommandManager();
            ArgumentConsumer<T> argCommand = (ArgumentConsumer<T>) manager.fromDescription(requestToWork.getCommandDescription());
            argCommand.setObj(requestToWork.getArgument());
            manager.executeCommand(requestToWork, request.getFrom(), request.getConnection(), clientManager);
        } catch (UserNotAuthenticatedException e) {
            logger.error("Что-то пошло не так во время аутентификации: " + e.getMessage());
        }
    }
}
