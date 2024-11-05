package commandManager.commands;

import client.ClientHandler;
import commandManager.CommandManager;

import responses.CommandStatusResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelpCommand implements BaseCommand {private static final Logger logger = LogManager.getLogger("HelpCommand");
    private CommandStatusResponse response;
    private ClientHandler client;

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
       logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }

    @Override
    public void setClient(ClientHandler clientManager) {
        this.client = clientManager;
    }
}
