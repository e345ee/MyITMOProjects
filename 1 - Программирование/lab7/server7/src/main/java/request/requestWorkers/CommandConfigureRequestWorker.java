package request.requestWorkers;

import commandLogic.CommandDescription;
import commandManager.CommandExporter;
import request.requests.ServerRequest;
import response.CommandConfigureResponseSender;
import responses.CommandDescriptionsResponse;

import java.util.ArrayList;

public class CommandConfigureRequestWorker implements RequestWorker {
    @Override
    public void workWithRequest(ServerRequest request) {
        ArrayList<CommandDescription> commands = CommandExporter.getCommandsToExport();
        CommandDescriptionsResponse response = new CommandDescriptionsResponse(commands);
        CommandConfigureResponseSender.sendResponse(response, request.getConnection(), request.getFrom());
    }
}
