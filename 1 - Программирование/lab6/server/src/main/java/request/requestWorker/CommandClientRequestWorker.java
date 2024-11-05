package request.requestWorker;

import commandManager.CommandManager;
import request.requests.ServerRequest;
import requests.CommandClientRequest;

//Обрабатывает команды, отправленные клиентом, и передает их в CommandManager для выполнения.
public class CommandClientRequestWorker implements RequestWorker {


    @Override
    public void workWithRequest(ServerRequest request) {
        CommandClientRequest requestToWork = (CommandClientRequest) request.getUserRequest();
        CommandManager manager = new CommandManager();
        manager.executeCommand(requestToWork, request.getFrom(), request.getConnection());
    }
}
