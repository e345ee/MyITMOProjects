package request;

import commandLogic.CommandDescription;
import exceptions.ServerNotAvailableException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import requests.CommandDescriptionsRequest;
import responses.CommandDescriptionsResponse;
import server.ServerConnectionHandler;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.util.ArrayList;

public class CommandDescriptionsRequestSender {

    private static final Logger logger = LogManager.getLogger("CommandDescriptionsRequestSender");

    public ArrayList<CommandDescription> sendRequestAndGetCommands() {
        ArrayList<CommandDescription> result = null;

        CommandDescriptionsRequest request = new CommandDescriptionsRequest();

        try {
            CommandDescriptionsResponse response = (CommandDescriptionsResponse) new RequestSender().sendRequest(request, ServerConnectionHandler.getCurrentConnection());
            result = response.getCommands();
        } catch (PortUnreachableException e) {
            logger.fatal("Сервер недоступен.");
            logger.fatal("Попробуйте позже.");
        } catch (ServerNotAvailableException e) {
            logger.fatal("Сервер недоступен.");
            logger.fatal("Невозможно получить команды.");
        } catch (IOException e) {
            logger.error("Что-то пошло не так.");
        }

        return result;
    }
}
