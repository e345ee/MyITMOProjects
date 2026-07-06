package request.requestWorker;

import request.requests.ServerRequest;

//Это интерфейс для создания классов-обработчиков запросов.
public interface RequestWorker {
    void workWithRequest(ServerRequest request);
}