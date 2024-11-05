package exceptions;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.CallerBack;

public class NotAvailableException extends Exception {

    private static final Logger logger = LogManager.getLogger("NotAvailableException");
    private final CallerBack deniedClient;

    public NotAvailableException(CallerBack deniedClient) {
        this.deniedClient = deniedClient;
        logger.info("Отказано в подключении: " + deniedClient);
    }

    public CallerBack getDeniedClient() {
        return deniedClient;
    }
}