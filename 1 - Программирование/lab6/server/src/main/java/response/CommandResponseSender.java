package response;

import logger.ServerLogger;
import request.CallerBack;
import responses.CommandStatusResponse;
import server.ServerConnection;

import java.io.IOException;

//Этот класс похож на ResponseSender, но работает с объектами типа CommandStatusResponse, которые представляют собой специфичные ответы на команды
public class CommandResponseSender {


    public static void sendResponse(CommandStatusResponse response, ServerConnection connection, CallerBack to) {
        try {
            if (response != null) {
                ResponseSender.sendResponse(response, connection, to);
            }
        } catch (IOException e) {
            ServerLogger.logger.fatal("Что-то пошло не так при отправке ответа", e);
        }
    }
}