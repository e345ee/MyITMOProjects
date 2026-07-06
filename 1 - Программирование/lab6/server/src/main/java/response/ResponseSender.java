package response;

import logger.ServerLogger;
import request.CallerBack;
import responses.AbsResponse;
import server.ServerConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

//Этот класс отвечает за отправку ответа от сервера клиенту:
public class ResponseSender {


    public static void sendResponse(AbsResponse response, ServerConnection connection, CallerBack to) throws IOException {
        if (response != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(response);
            connection.sendData(bos.toByteArray(), to.getAddress(), to.getPort());
            ServerLogger.logger.info("Ответ отправлен.");
        }
    }
}