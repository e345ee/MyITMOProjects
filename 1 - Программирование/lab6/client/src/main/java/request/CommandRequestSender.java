package request;

import commandLogic.CommandDescription;
import exceptions.ServerNotAvailableException;
import requests.CommandClientRequest;
import responses.CommandStatusResponse;
import server.ServerConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.PortUnreachableException;
//Класс для отправки команд на сервер:
//
//CommandRequestSender:
//Отправляет команду CommandDescription и аргументы на сервер, используя RequestSender.
//Обрабатывает такие ошибки, как недоступность сервера (PortUnreachableException) или просроченная сессия (ServerNotAvailableException).
public class CommandRequestSender {
    private static final Logger logger = LogManager.getLogger("CommandRequestSender");

    public CommandStatusResponse sendCommand(CommandDescription command, String[] args, ServerConnection connection) {
        CommandStatusResponse response = null;
        try {
            var rq = new CommandClientRequest(command, args);
            logger.info("Отправка команды.");
            response = (CommandStatusResponse) new RequestSender().sendRequest(rq, connection);
        } catch (PortUnreachableException e) {
            logger.warn("Сервер недоступен.");
        } catch (ServerNotAvailableException e) {
            logger.error("Сессия просрочена.");
            logger.warn("Приложение будет закрыто.");
            System.exit(0);
        } catch (IOException e) {
            logger.error("Что-то пошло не так", e);
        }
        return response;
    }
}