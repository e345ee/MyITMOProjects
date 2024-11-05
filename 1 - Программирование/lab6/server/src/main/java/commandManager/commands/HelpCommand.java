package commandManager.commands;

import commandManager.CommandManager;
import logger.ServerLogger;
import responses.CommandStatusResponse;

import java.util.Optional;

public class HelpCommand implements BaseCommand {
    private CommandStatusResponse response;

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescr() {
        return "Выводит информацию о командах.";
    }


    @Override
    public void execute(String[] args) {
        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        CommandManager manager = new CommandManager();

        StringBuilder sb = new StringBuilder();

        if (args.length == 1) {
            manager.getCommands().forEach((name, command) -> sb.append(name).append(" ").append(command.getArgs()).append(" : ").append(command.getDescr()).append('\n'));

            response = CommandStatusResponse.ofString(sb.toString());
        } else {
            response = CommandStatusResponse.ofString("Неверно заданы аргументы");

        }

        response = CommandStatusResponse.ofString(sb.toString());
        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}
