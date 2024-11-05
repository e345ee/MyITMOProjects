package commandManager.commands;
import client.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import responses.CommandStatusResponse;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HistoryCommand implements BaseCommand {
    private static final Logger logger = LogManager.getLogger("HistoryCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

    /**
     * The maximum size of the command history
     */
    private static final int QUEUE_SIZE = 13;

    /**
     * An array containing the commands in the history
     */
    private static List<String> commandHistory;

    static {
        commandHistory = Collections.synchronizedList(new LinkedList<>());
    }

    @Override
    public String getName() {
        return "history";
    }

    @Override
    public String getDescr() {
        return "Выводит последние 13 команд без аргументов.";
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            throw new IllegalArgumentException("Команда " + getName() + " не имеет аргументов.");
        }

        // Create a synchronized block to safely access the commandHistory
        synchronized (commandHistory) {
            response = CommandStatusResponse.ofString(IntStream.range(0, commandHistory.size()) // Создаем поток с индексами
                    .mapToObj(i -> (i + 1) + ") " + commandHistory.get(i)) // Форматируем каждую строку с индексом
                    .collect(Collectors.joining("\n")));
        }

        logger.info(response.getResponse());
    }

    /**
     * Adds a command to the history
     */
    public static void addToHistory(String value) {
        synchronized (commandHistory) {
            if (commandHistory.size() >= QUEUE_SIZE) {
                // Check if list is not empty before removing
                if (!commandHistory.isEmpty()) {
                    commandHistory.remove(0); // Remove the first element (FIFO principle)
                }
            }
            commandHistory.add(value); // Add new command to the end of the list
        }
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
