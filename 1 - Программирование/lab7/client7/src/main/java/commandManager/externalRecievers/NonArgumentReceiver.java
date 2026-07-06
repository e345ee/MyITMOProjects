package commandManager.externalRecievers;

import commandLogic.CommandDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;
import request.CommandRequestSender;
import responses.CommandStatusResponse;
import server.ServerConnectionHandler;

public class NonArgumentReceiver implements ExternalBaseReceiver {

    private static final Logger logger = LogManager.getLogger("NonArgumentReceiver");

    @Override
    public boolean receiveCommand(String name, char[] passwd, CommandDescription command, String[] args) {
        CommandStatusResponse response = new CommandRequestSender().sendCommand(name, passwd, command, args, ServerConnectionHandler.getCurrentConnection());
        if (response != null) {
            logger.info("Статус: " + response.getStatusCode());
            logger.info("Ответ: \n" + response.getResponse());
            return true;
        }
        return false;
    }
}