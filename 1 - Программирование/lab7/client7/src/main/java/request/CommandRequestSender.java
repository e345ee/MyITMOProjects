package request;

import commandLogic.CommandDescription;
import exceptions.ServerNotAvailableException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import requests.CommandClientRequest;
import responses.CommandStatusResponse;
import server.ServerConnection;

import java.io.IOException;
import java.net.PortUnreachableException;

public class CommandRequestSender {
    private static final Logger logger = LogManager.getLogger("CommandRequestSender");

    public CommandStatusResponse sendCommand(String name, char[] passwd, CommandDescription command, String[] args, ServerConnection connection) {
        CommandStatusResponse response = null;
        try {
            var rq = new CommandClientRequest(name, passwd, command, args);
            logger.info("Отправка команды.");
            response = (CommandStatusResponse) new RequestSender().sendRequest(rq, connection);
        } catch (PortUnreachableException e) {
            logger.warn("Сервер недоступен. Попробуйте позже.");
        } catch (ServerNotAvailableException e) {
            logger.error("Ваша сессия устарела.");
            logger.warn("Приложение будет выключено.");
            System.exit(0);
        } catch (IOException e) {
            logger.error("Что-то пошло не так", e);
        }
        return response;
    }
}