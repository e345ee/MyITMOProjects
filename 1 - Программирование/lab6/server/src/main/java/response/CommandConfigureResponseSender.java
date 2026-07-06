package response;

import logger.ServerLogger;
import request.CallerBack;
import responses.CommandDescriptionsResponse;
import server.ServerConnection;

import java.io.IOException;

//Этот класс работает с объектами CommandDescriptionsResponse, которые отправляются клиенту, чтобы предоставить описание доступных команд.
public class CommandConfigureResponseSender {

    public static void sendResponse(CommandDescriptionsResponse response, ServerConnection connection, CallerBack to) {
        try {
            if (response != null) {
                ResponseSender.sendResponse(response, connection, to);
            }
        } catch (IOException e) {
            ServerLogger.logger.fatal("Что-то пошло не так при отправке ответа.", e);
        }
    }
}