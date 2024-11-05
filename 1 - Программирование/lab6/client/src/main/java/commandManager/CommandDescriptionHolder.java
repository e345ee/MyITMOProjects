package commandManager;

import commandLogic.CommandDescription;

import java.util.ArrayList;

//Это синглтон, который хранит загруженные с сервера описания команд
public class CommandDescriptionHolder {

    private static CommandDescriptionHolder instance;
    ArrayList<CommandDescription> commands;

    private CommandDescriptionHolder(ArrayList<CommandDescription> commands) {
        this.commands = commands;
    }

    public static void initialize(ArrayList<CommandDescription> commands) {
        instance = new CommandDescriptionHolder(commands);
    }

    public static CommandDescriptionHolder getInstance() {
        return instance;
    }

    public ArrayList<CommandDescription> getCommands() {
        return commands;
    }
}