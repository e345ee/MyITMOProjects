package request.requestWorkers;

import client.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.requests.ServerRequest;
import requests.RegRequest;
import response.ResponseSender;
import responses.RegResponse;

public class RegWorker implements RequestWorker{
    private static final Logger logger = LogManager.getLogger("RegWorker");
    @Override
    public void workWithRequest(ServerRequest request) {
        RegRequest requestToWork = (RegRequest) request.getUserRequest();
        ClientHandler manager = new ClientHandler(requestToWork.getName(), requestToWork.getPasswd());
        boolean reg = manager.regUser();
        RegResponse response = new RegResponse(reg);
        ResponseSender.sendResponse(response, request.getConnection(), request.getFrom());
    }
}
