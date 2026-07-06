package request;

import exceptions.ServerNotAvailableException;
import requests.AbsRequest;
import response.ResponseReader;
import responses.AbsResponse;
import server.ServerConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

//Класс отвечает за отправку запросов на сервер:
//
//RequestSender:
//Отправляет сериализованный объект запроса (наследника AbsRequest) на сервер.
//После отправки запроса ожидает ответа, используя ResponseReader.
//Логирует процесс отправки и получения данных.
public class RequestSender {
    private static final Logger logger = LogManager.getLogger("RequestSender");

    public AbsResponse sendRequest(AbsRequest request, ServerConnection connection) throws IOException, ServerNotAvailableException {
        AbsResponse response = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(request);
            logger.info("Запрос отправлен.");
            InputStream responseStream = connection.sendData(bos.toByteArray());
            if (responseStream != null) {
                ResponseReader reader = new ResponseReader(responseStream);
                response = reader.readObject();
                logger.info("Запрос получен.");
            } else logger.info("Получено null.");
        } catch (ClassNotFoundException e) {
            logger.error("Ответ не распознан как класс.");
        }
        return response;
    }
}
