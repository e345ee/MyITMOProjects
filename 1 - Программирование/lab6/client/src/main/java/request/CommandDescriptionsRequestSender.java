package request;

import commandLogic.CommandDescription;
import exceptions.ServerNotAvailableException;
import requests.CommandDescriptionsRequest;
import responses.CommandDescriptionsResponse;
import server.ServerConnectionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.util.ArrayList;

//Класс для отправки запроса на получение списка доступных команд:
//
//CommandDescriptionsRequestSender:
//Отправляет запрос на сервер для получения списка доступных команд.
//Получает ответ с описанием команд, используя класс RequestSender.
//Логирует возможные ошибки во время получения данных.
public class CommandDescriptionsRequestSender {

    private static final Logger logger = LogManager.getLogger("CommandDescriptionsRequestSender");

    public ArrayList<CommandDescription> sendRequestAndGetCommands() {
        ArrayList<CommandDescription> result = null;

        CommandDescriptionsRequest request = new CommandDescriptionsRequest();

        try {
            CommandDescriptionsResponse response = (CommandDescriptionsResponse) new RequestSender().sendRequest(request, ServerConnectionHandler.getCurrentConnection());
            result = response.getCommands();
        } catch (PortUnreachableException e) {
            logger.fatal("Сервер недоступен.");
            logger.fatal("Невозможно получить команды, потому что сервер недоступен.");
        } catch (ServerNotAvailableException e) {
            logger.fatal("Сервер занят.");
            logger.fatal("Невозможно получить команды, потому что сервер занят.");
        } catch (IOException e) {
            logger.error("Что-то пошло не так.");
        }

        return result;
    }
}