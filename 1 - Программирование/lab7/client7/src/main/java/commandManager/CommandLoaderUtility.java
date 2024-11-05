package commandManager;

import commandLogic.CommandDescription;
import request.CommandDescriptionsRequestSender;

import java.util.ArrayList;

public class CommandLoaderUtility {
    public static void initializeCommands() {
        ArrayList<CommandDescription> commands = new CommandDescriptionsRequestSender().sendRequestAndGetCommands();
        CommandDescriptionHolder.initialize(commands);
    }
}