package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;
import managers.commandManger.CommandExecutor;
import managers.commandManger.InputMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.regex.Pattern;

/**
 * Класс представляющий реализацию команды исполнения скрипта.
 */
public class ExecuteScript extends Command {

    /**
     * Базовый конструктор задающий, что аргументов у команды должен быть.
     */
    public ExecuteScript() {
        super(true);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return "execute_script";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return "Читает и исполняет скрипт.";
    }


    /**
     * Проверяет рекурсию.
     * @param path путь до файла.
     * @param stack история файлов внутри цикла исполнения скрипта.
     * @return true если рекурсия найдена.
     * @throws IOException Если имя файла после команды было задано с ошибкой.
     */
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

    /**
     * Выполняет скрипт.
     * @throws IllegalArgumentException если произошла ошибка исполнения скрипта.
     */
    @Override
    public void execute() throws IllegalArgumentException {
        if (checkArgument(getArgument())) {
            try {
                ProductManager manager = ProductManager.getInstance();

                CommandExecutor executor = new CommandExecutor();
                if (checkRecursion(Path.of((String) this.getArgument()), new ArrayDeque<>())) {
                    System.out.println("При анализе скрипта обнаружена рекурсия. Устраните ее перед исполнением.");
                    return;
                }

                System.out.println("--------------------------------------------------");
                System.out.println("Начало выполнения скрипта: " + this.getArgument());
                System.out.println("--------------------------------------------------");

                executor.startExecuting(new FileInputStream((String) this.getArgument()), InputMode.NON_USER_INPUT);

                System.out.println("--------------------------------------------------");
                System.out.println("Конец выполнения скрипта: " + this.getArgument() );
                System.out.println("--------------------------------------------------");

            } catch (InvalidPathException e) {
                System.out.println("Неподходящий путь к файлу.");
                throw new IllegalArgumentException(e);
            } catch (FileNotFoundException | NoSuchFileException e) {
                System.out.println("Файл не найден.");
            }  catch (IOException e) {
                System.out.println("Что-то пошло не так:. (" + e.getMessage() + ")");
            }
        }
    }

    /**
     * Проверяет переданный аргумент.
     * @param inputArgument аргумент команды.
     * @return возвращает true, если
     */
    @Override
    public boolean checkArgument(Object inputArgument) {
        if (inputArgument == null) {
            System.out.println("Execute_script имеет один аргумент!");
            return false;
        } else return inputArgument instanceof String;
    }


}
