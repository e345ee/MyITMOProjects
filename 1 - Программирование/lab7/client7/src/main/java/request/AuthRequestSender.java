package request;


import exceptions.ServerNotAvailableException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import requests.AuthRequest;
import responses.AuthResponse;
import server.ServerConnection;

import java.io.IOException;
import java.net.PortUnreachableException;


public class AuthRequestSender {
    private static final Logger logger = LogManager.getLogger("AuthRequestSender");

    public AuthResponse sendAuthData(String name, char[] passwd, ServerConnection connection) {
        AuthResponse response = null;
        try {
            AuthRequest rq = new AuthRequest(name, passwd);
            logger.info("Отправка запроса на авторизацию.");
            response = (AuthResponse) new RequestSender().sendRequest(rq, connection);
        } catch (PortUnreachableException e) {
            logger.warn("Сервер недоступен, попробуйте позже.");
        } catch (ServerNotAvailableException e) {
            logger.error("Сессия устарела.");
            logger.warn("Приложение будет отключено.");
            System.exit(0);
        } catch (IOException e) {
            logger.error("Что-то пошло не так.", e);
        }
        return response;
    }
}