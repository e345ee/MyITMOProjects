package responses;

import commandLogic.CommandDescription;

import java.util.ArrayList;

//Этот класс возвращает список описаний команд.
public class CommandDescriptionsResponse extends AbsResponse {
    private final ArrayList<CommandDescription> commands;


    public CommandDescriptionsResponse(ArrayList<CommandDescription> commands) {
        this.commands = commands;
    }

    public ArrayList<CommandDescription> getCommands() {
        return commands;
    }
}
