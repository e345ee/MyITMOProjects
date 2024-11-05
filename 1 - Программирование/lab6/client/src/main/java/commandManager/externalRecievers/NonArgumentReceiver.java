package commandManager.externalRecievers;
import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandLogic.CommandDescription;
import request.CommandRequestSender;
import responses.CommandStatusResponse;
import server.ServerConnectionHandler;

//Этот обработчик отвечает за команды, которые не требуют аргументов.
public class NonArgumentReceiver implements ExternalBaseReceiver {

    private static final Logger logger = LogManager.getLogger("NonArgumentReceiver");

    @Override
    public boolean receiveCommand(CommandDescription command, String[] args) {
        CommandStatusResponse response = new CommandRequestSender().sendCommand(command, args, ServerConnectionHandler.getCurrentConnection());
        if (response != null) {
            logger.info("Статус: " + response.getStatusCode());
            logger.info("Ответ: \n" + response.getResponse());
            return true;
        }
        return false;
    }
}