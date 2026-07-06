package commandManager.externalRecievers;

import commandLogic.CommandDescription;
import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;
import commandManager.CommandDescriptionHolder;
import commandManager.CommandExecutor;
import commandManager.CommandMode;
import exceptions.CommandsNotLoadedException;
import exceptions.WrongAmountOfArgumentsException;
import utilities.CheckerArgument;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Objects;

import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExecuteScriptReceiver implements ExternalBaseReceiver {
    private static final Logger logger = LogManager.getLogger("ExecuteScriptReceiver");

    @Override
    public boolean receiveCommand(CommandDescription commandDescription, String[] args) throws IllegalArgumentException, WrongAmountOfArgumentsException {

        if (!Objects.equals(commandDescription.getName(), "execute_script")) return true;

        CheckerArgument.checkArgumentsOrThrow(args.length, 1);

        try {
            CommandExecutor executor = new CommandExecutor(CommandDescriptionHolder.getInstance().getCommands(), new FileInputStream(Path.of(args[1]).toFile()), CommandMode.NonUserMode);
            if (checkRecursion(Path.of(args[1]), new ArrayDeque<>())) {
                logger.error("При анализе скрипта обнаружена рекурсия. Устраните ее перед исполнением.");

                return false;
            }
            logger.info("Выполнение скрипта " + args[1]);

            executor.startExecuting();
        } catch (InvalidPathException e) {
            System.out.println("Аргумент невалиден.");
            throw new IllegalArgumentException(e);
        } catch (FileNotFoundException | NoSuchFileException e) {
            System.out.println("Файл не найден.");
        } catch (SecurityException e) {
            System.out.println("Доступ запрещен.");
        } catch (IOException e) {
            System.out.println("Что-то пошло не так. Попробуйте снова. (" + e.getMessage() + ")");
        } catch (CommandsNotLoadedException e) {
            System.out.println("Команды нет.");
        }
        return false;
    }

    private boolean checkRecursion(Path path, ArrayDeque<Path> stack) throws IOException {
        if (stack.contains(path)) return true;
        stack.addLast(path);
        String str = Files.readString(path);
        Pattern pattern = Pattern.compile("execute_script .*");
        var patternMatcher = pattern.matcher(str);
        while (patternMatcher.find()) {
            Path newPath = Path.of(patternMatcher.group().split("\\s+")[1]);
            if (checkRecursion(newPath, stack)) return true;
        }
        stack.removeLast();
        return false;
    }
}
