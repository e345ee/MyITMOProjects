package request.requestWorkers;

import request.requests.ServerRequest;

public interface RequestWorker {
    void workWithRequest(ServerRequest request);
}
