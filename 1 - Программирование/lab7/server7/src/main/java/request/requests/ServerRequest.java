package request.requests;

import request.CallerBack;
import requests.AbsRequest;
import server.ServerConnection;

public class ServerRequest {
    private final AbsRequest request;
    private final CallerBack from;
    private final ServerConnection connection;

    public ServerRequest(AbsRequest request, CallerBack from, ServerConnection connection) {
        this.request = request;
        this.from = from;
        this.connection = connection;
    }

    public AbsRequest getUserRequest() {
        return request;
    }

    public ServerConnection getConnection() {
        return connection;
    }

    public CallerBack getFrom() {
        return from;
    }
}
