package request;

import commandLogic.CommandDescription;
import exceptions.ServerNotAvailableException;
import requests.ArgumentCommandClientRequest;
import responses.CommandStatusResponse;
import server.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.net.PortUnreachableException;
//Класс для отправки команд с аргументами на сервер:
//
//ArgumentRequestSender<T>:
//Отправляет команду и аргумент на сервер.
//Обрабатывает возможные ошибки, такие как недоступность сервера или устаревшая сессия.
public class ArgumentRequestSender<T extends Serializable> {

    private static final Logger logger = LogManager.getLogger("ArgumentRequestSender");

    public CommandStatusResponse sendCommand(CommandDescription command, String[] args, T argument, ServerConnection connection) {
        CommandStatusResponse response = null;
        try {
            ArgumentCommandClientRequest<T> rq = new ArgumentCommandClientRequest<>(command, args, argument);
            logger.info("Отправка запроса.");
            response = (CommandStatusResponse) new RequestSender().sendRequest(rq, connection);
        } catch (PortUnreachableException e) {
            logger.warn("Сервер недоступен.");
        } catch (ServerNotAvailableException e) {
            logger.error("Сессия устарела.");
            logger.warn("Приложение будет закрыто.");
            System.exit(0);
        } catch (IOException e) {
            logger.error("Что-то пошло не так.");
        }
        return response;
    }
}