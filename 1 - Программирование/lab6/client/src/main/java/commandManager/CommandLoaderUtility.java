package commandManager;

import commandLogic.CommandDescription;
import request.CommandDescriptionsRequestSender;

import java.util.ArrayList;

//Этот утилитный класс отвечает за загрузку и инициализацию команд.
//initializeCommands(): Использует CommandDescriptionsRequestSender для запроса доступных команд с сервера, а затем передает их в CommandDescriptionHolder для хранения.
public class CommandLoaderUtility {
    public static void initializeCommands() {
        ArrayList<CommandDescription> commands = new CommandDescriptionsRequestSender().sendRequestAndGetCommands();
        CommandDescriptionHolder.initialize(commands);
    }
}
