package commandManager.commands;

import responses.CommandStatusResponse;

public class ExecuteScriptCommand implements BaseCommand {
    private CommandStatusResponse response;
    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescr() {
        return "Читает и выполняет скрипт.";
    }

    @Override
    public String getArgs() {
        return "file_path";
    }

    @Override
    public void execute(String[] args) throws IllegalArgumentException {
        if (args.length>2){
            throw new IllegalArgumentException("Команда " + getName()+ " имеет только один аргумент.");
        }

        response = CommandStatusResponse.ofString("Сервер готов для исполнения команд!");
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}
