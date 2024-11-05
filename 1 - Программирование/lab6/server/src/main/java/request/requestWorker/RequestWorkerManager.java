package request.requestWorker;

import exceptions.UnsupportedRequestException;
import logger.ServerLogger;
import request.requests.ServerRequest;
import requests.AbsRequest;
import requests.ArgumentCommandClientRequest;
import requests.CommandClientRequest;
import requests.CommandDescriptionsRequest;

import java.util.LinkedHashMap;
import java.util.Optional;

//Это основной класс для обработки входящих запросов. Он содержит набор обработчиков (RequestWorker),
// каждый из которых обрабатывает определенный тип запроса.
public class RequestWorkerManager {



    private final LinkedHashMap<Class<?>, RequestWorker> workers = new LinkedHashMap<>();

    public RequestWorkerManager() {
        workers.put(AbsRequest.class, new BaseRequestWorker());
        workers.put(CommandClientRequest.class, new CommandClientRequestWorker());
        workers.put(ArgumentCommandClientRequest.class, new ArgumentCommandClientRequestWorker<>());
        workers.put(CommandDescriptionsRequest.class, new CommandConfigureRequestWorker());
    }

    public void workWithRequest(ServerRequest request) {
        try {
            Optional.ofNullable(workers.get(request.getUserRequest().getClass())).orElseThrow(() -> new UnsupportedRequestException("Указанный запрос не может быть обработан")).workWithRequest(request);
        } catch (UnsupportedRequestException ex) {
            ServerLogger.logger.error("Получен неверный запрос.");
        }
    }
}