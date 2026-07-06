package response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.CallerBack;
import responses.CommandDescriptionsResponse;
import server.ServerConnection;

public class CommandConfigureResponseSender {
    private static final Logger logger = LogManager.getLogger("CommandConfigureResponseSender");

    public static void sendResponse(CommandDescriptionsResponse response, ServerConnection connection, CallerBack to) {
        if (response != null) {
            ResponseSender.sendResponse(response, connection, to);
        }
    }
}
