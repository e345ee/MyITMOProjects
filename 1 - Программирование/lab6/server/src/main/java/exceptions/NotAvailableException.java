package exceptions;

import logger.ServerLogger;
import request.CallerBack;

public class NotAvailableException extends Exception {

    private final CallerBack deniedClient;

    public NotAvailableException(CallerBack deniedClient) {
        this.deniedClient = deniedClient;
        ServerLogger.logger.info("Denied connection: " + deniedClient);
    }

    public CallerBack getDeniedClient() {
        return deniedClient;
    }
}