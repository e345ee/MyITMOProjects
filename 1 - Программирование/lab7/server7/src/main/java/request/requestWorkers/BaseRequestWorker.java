package request.requestWorkers;

import request.requests.ServerRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseRequestWorker implements RequestWorker {

    private static final Logger logger = LogManager.getLogger("BaseRequestWorker");

    @Override
    public void workWithRequest(ServerRequest request) {
    }
}
