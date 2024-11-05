package request.requestWorker;

import commandManager.CommandManager;
import commandManager.commands.ArgumentConsumer;
import request.requests.ServerRequest;
import requests.ArgumentCommandClientRequest;

//Этот обработчик работает с запросами, которые содержат команды с аргументами.
public class ArgumentCommandClientRequestWorker<T, Y> implements RequestWorker {


    @Override
    public void workWithRequest(ServerRequest request) {
        ArgumentCommandClientRequest<T> requestToWork = (ArgumentCommandClientRequest<T>) request.getUserRequest();
        CommandManager manager = new CommandManager();
        ArgumentConsumer<T> argCommand = (ArgumentConsumer<T>) manager.fromDescription(requestToWork.getCommandDescription());
        argCommand.setObj(requestToWork.getArgument());
        manager.executeCommand(requestToWork, request.getFrom(), request.getConnection());
    }
}