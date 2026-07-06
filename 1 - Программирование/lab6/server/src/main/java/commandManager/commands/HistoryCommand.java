package commandManager.commands;

import logger.ServerLogger;
import responses.CommandStatusResponse;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HistoryCommand implements BaseCommand {

    private CommandStatusResponse response;
    /**
     * The maximum size of the command history
     */
    private static final int QUEUE_SIZE = 13;
    /**
     * An array containing the commands in the history
     */
    private static LinkedList<String> commandHistory;

    static {
        commandHistory = new LinkedList<>();
    }

    @Override
    public String getName() {
        return "history";
    }

    @Override
    public String getDescr() {
        return "Выводит последние 13 команд  без аргументов.";
    }

    @Override
    public void execute(String[] args) {
        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        response = CommandStatusResponse.ofString(IntStream.range(0, commandHistory.size()) // Создаем поток с индексами
                .mapToObj(i -> (i + 1) + ") " + commandHistory.get(i)) // Форматируем каждую строку с индексом
                .collect(Collectors.joining("\n")));
        ServerLogger.logger.info(response.getResponse());
    }

    /**
     * Prints the command history
     */
    public static void addToHistory(String value) {
        if (commandHistory.size() >= QUEUE_SIZE) {
            commandHistory.removeFirst();
        }
        commandHistory.addLast(value);
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}
