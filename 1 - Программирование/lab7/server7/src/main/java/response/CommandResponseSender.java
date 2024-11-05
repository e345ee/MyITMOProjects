package response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.CallerBack;
import responses.CommandStatusResponse;
import server.ServerConnection;

public class CommandResponseSender {
    private static final Logger logger = LogManager.getLogger("CommandResponseSender");

    public static void sendResponse(CommandStatusResponse response, ServerConnection connection, CallerBack to) {
        if (response != null) {
            ResponseSender.sendResponse(response, connection, to);
        }
    }
}
