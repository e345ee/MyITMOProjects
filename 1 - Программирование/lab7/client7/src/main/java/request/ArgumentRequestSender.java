package request;

import java.io.IOException;
import java.io.Serializable;
import java.net.PortUnreachableException;


import commandLogic.CommandDescription;
import exceptions.ServerNotAvailableException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import requests.ArgumentCommandClientRequest;
import responses.CommandStatusResponse;
import server.ServerConnection;

public class ArgumentRequestSender<T extends Serializable> {

    private static final Logger logger = LogManager.getLogger("ArgumentRequestSender");

    public CommandStatusResponse sendCommand(String name, char[] passwd, CommandDescription command, String[] args, T argument, ServerConnection connection) {
        CommandStatusResponse response = null;
        try {
            ArgumentCommandClientRequest<T> rq = new ArgumentCommandClientRequest<>(name, passwd, command, args, argument);
            logger.info("Отправка команды с аргументом");
            response = (CommandStatusResponse) new RequestSender().sendRequest(rq, connection);
        } catch (PortUnreachableException e) {
            logger.warn("Сервер недоступен, попробуйте позже.");
        } catch (ServerNotAvailableException e) {
            logger.error("Сессия устарела.");
            logger.warn("Приложение будет отключено.");
            System.exit(0);
        } catch (IOException e) {
            logger.error("Что-то пошло не так.");
        }
        return response;
    }
}
