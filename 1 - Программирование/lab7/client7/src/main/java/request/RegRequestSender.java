package request;

import responses.RegResponse;
import requests.*;
import server.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import exceptions.ServerNotAvailableException;

import java.net.PortUnreachableException;
import java.io.IOException;


public class RegRequestSender {
    private static final Logger logger = LogManager.getLogger("RegRequestSender");

    public RegResponse sendRegData(String name, char[] passwd, ServerConnection connection) {
        RegResponse response = null;
        try {
            RegRequest rq = new RegRequest(name, passwd);
            logger.info("Отправка авторизации...");
            response = (RegResponse) new RequestSender().sendRequest(rq, connection);
        } catch (PortUnreachableException e) {
            logger.warn("Сервер недоступен.");
        } catch (ServerNotAvailableException e) {
            logger.error("Ваша сессия устарела. Попробуйте подключится позже.");
            logger.warn("Приложение будет отключено.");
            System.exit(0);
        } catch (IOException e) {
            logger.error("Что-то пошло не так.", e);
        }
        return response;
    }
}