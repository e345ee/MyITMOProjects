package request.requestWorker;

import logger.ServerLogger;
import request.requests.ServerRequest;

//Это базовый обработчик запросов. В данном случае он просто выводит сообщение в лог.
public class BaseRequestWorker implements RequestWorker {



    @Override
    public void workWithRequest(ServerRequest request) {
        ServerLogger.logger.info("Base Request");
    }
}