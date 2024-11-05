package response;

import multithreading.MultithreadingManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.CallerBack;
import responses.AbsResponse;
import server.ServerConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;

public class ResponseSender {
    private static final Logger logger = LogManager.getLogger("ResponseSender");

    public static void sendResponse(AbsResponse response, ServerConnection connection, CallerBack to) {
        if (response != null) {
            ExecutorService responseThreadPool = MultithreadingManager.getResponseThreadPool();
            responseThreadPool.submit(() -> {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(response);
                    connection.sendData(bos.toByteArray(), to.getAddress(), to.getPort());
                    logger.info("Ответ отправлен.");
                } catch (IOException e) {
                    logger.error("Возникла ошибка при отправке.", e);
                }
            });
        }
    }
}
