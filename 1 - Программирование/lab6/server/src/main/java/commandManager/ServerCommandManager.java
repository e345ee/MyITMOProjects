package commandManager;

import commandLogic.CommandDescription;
import commandManager.commands.BaseCommand;
import commandManager.commands.ExitServerCommand;
import commandManager.commands.SaveCommand;
import exceptions.UnknownCommandException;
import logger.ServerLogger;

import java.util.LinkedHashMap;
import java.util.Optional;

public class ServerCommandManager {


    final LinkedHashMap<String, BaseCommand> serverCommands;


    public ServerCommandManager() {
        serverCommands = new LinkedHashMap<>();

        serverCommands.put("save", new SaveCommand());
        serverCommands.put("exit_server", new ExitServerCommand());
    }


    public LinkedHashMap<String, BaseCommand> getServerCommands() {
        return serverCommands;
    }


    public void executeCommand(String[] args) {
        try {
            BaseCommand baseCommand = Optional.ofNullable(serverCommands.get(args[0])).orElseThrow(() -> new UnknownCommandException("Указанная команда не была обнаружена"));
            baseCommand.execute(args);
            System.out.println(baseCommand.getResponse().getResponse());
        } catch (Exception e) {
            ServerLogger.logger.fatal("Что-то пошло не так!");
        }
    }

    public BaseCommand fromDescription(CommandDescription description) {
        return serverCommands.get(description.getName());
    }
}