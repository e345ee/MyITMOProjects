package request.requestWorker;

import commandLogic.CommandDescription;
import commandManager.CommandExporter;
import request.requests.ServerRequest;
import response.CommandConfigureResponseSender;
import responses.CommandDescriptionsResponse;

import java.util.ArrayList;

//Этот обработчик отвечает за отправку конфигурационного ответа с описанием команд, доступных на сервере.
public class CommandConfigureRequestWorker implements RequestWorker {
    @Override
    public void workWithRequest(ServerRequest request) {
        ArrayList<CommandDescription> commands = CommandExporter.getCommandsToExport();
        CommandDescriptionsResponse response = new CommandDescriptionsResponse(commands);
        CommandConfigureResponseSender.sendResponse(response, request.getConnection(), request.getFrom());
    }
}
