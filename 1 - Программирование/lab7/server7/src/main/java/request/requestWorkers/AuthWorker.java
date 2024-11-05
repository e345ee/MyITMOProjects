package request.requestWorkers;

import client.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.requests.ServerRequest;
import requests.AuthRequest;
import response.ResponseSender;
import responses.AuthResponse;

public class AuthWorker implements RequestWorker{
    private static final Logger logger = LogManager.getLogger("AuthWorker");
    @Override
    public void workWithRequest(ServerRequest request) {
        AuthRequest requestToWork = (AuthRequest) request.getUserRequest();
        ClientHandler manager = new ClientHandler(requestToWork.getName(), requestToWork.getPasswd());
        boolean auth = manager.authUser();
        AuthResponse response = new AuthResponse(auth);
        ResponseSender.sendResponse(response, request.getConnection(), request.getFrom());
    }
}