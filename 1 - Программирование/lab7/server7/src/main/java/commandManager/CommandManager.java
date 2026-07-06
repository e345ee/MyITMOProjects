package commandManager;

import client.ClientHandler;
import commandLogic.CommandDescription;
import commandManager.commands.*;

import exceptions.UnknownCommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.CallerBack;
import requests.CommandClientRequest;
import response.CommandResponseSender;
import responses.CommandStatusResponse;
import server.ServerConnection;

import java.util.LinkedHashMap;
import java.util.Optional;

public class CommandManager {

    private static final Logger logger = LogManager.getLogger("CommandManager");
    final LinkedHashMap<String, BaseCommand> commands;

    /**
     * Setup command manager and all of its commands.
     */
    public CommandManager() {
        commands = new LinkedHashMap<>();

        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("filter_by_part_number", new FilterByPartNumberCommand());
        commands.put("filter_starts_with_part_number", new FilterStartsWithPartNumberCommand());
        commands.put("generate", new GeneratorCommand() );
         commands.put("update", new UpdateCommand());
        commands.put("clear", new ClearCommand());
        commands.put("save", new SaveCommand());
        commands.put("execute_script", new ExecuteScriptCommand());
        commands.put("exit", new ExitCommand());
        commands.put("history", new HistoryCommand());
        commands.put("remove_by_id", new RemoveByIdCommand());
        commands.put("add", new AddCommand());
        commands.put("add_if_min", new AddIfMinCommand());
        commands.put("remove_all_by_manufacturer", new RemoveAllByManufacturerCommand());


    }

    /**
     * Get all commands from manager.
     *
     * @return Map of loaded commands
     */
    public LinkedHashMap<String, BaseCommand> getCommands() {
        return commands;
    }

    /**
     * Universe method for command executing.
     *
     * @param command       request
     * @param clientManager
     */
    public void executeCommand(CommandClientRequest command, CallerBack requester, ServerConnection answerConnection, ClientHandler clientManager) {
        CommandStatusResponse response;
        try {
            BaseCommand baseCommand = Optional.ofNullable(commands.get(command.getCommandDescription().getName())).orElseThrow(() -> new UnknownCommandException("Указанная команда не была обнаружена"));
            HistoryCommand.addToHistory(baseCommand.getName());

            baseCommand.setClient(clientManager);

            baseCommand.execute(command.getLineArgs());
            response = baseCommand.getResponse();
        } catch (IllegalArgumentException | NullPointerException e) {
            response = new CommandStatusResponse("Выполнение команды пропущено из-за неправильных предоставленных аргументов! (" + e.getMessage() + ")", -38);
            logger.fatal(response.getResponse(), e);
        } catch (IndexOutOfBoundsException e) {
            response = new CommandStatusResponse("В команде предоставлено неправильное количество аргументов. Возможно, вам нужно обновить клиент", -68);
            logger.fatal(response.getResponse(), e);
        } catch (Exception e) {
            response = new CommandStatusResponse("В командном менеджере произошла непредвиденная ошибка!", -79);
            logger.fatal(response.getResponse(), e);
        }
        CommandResponseSender.sendResponse(response, answerConnection, requester);
    }

    public BaseCommand fromDescription(CommandDescription description) {
        return commands.get(description.getName());
    }
}