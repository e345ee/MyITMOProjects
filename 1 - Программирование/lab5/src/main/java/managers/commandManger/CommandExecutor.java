package managers.commandManger;

import exceptions.CommandArgumentException;
import exceptions.CommandInterruptedException;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс запускающий в работу считывание команд, а затем посылает запрос на их выполнение.
 */
public class CommandExecutor {

    /**
     * Метод начинает бесконечно считывать пользовательский ввод командной строки
     * в поисках команды. Если ввод соответствует команде в списке, он вызывает у команды метод исполнения.
     * @param input
     * @param mode
     */
    public void startExecuting(InputStream input, InputMode mode){
        Scanner commandScanner = new Scanner(input);
        CommandManager commandManager = new CommandManager(mode, commandScanner);

        while (commandScanner.hasNext()){
            String line = commandScanner.nextLine().trim();
            if (line.isBlank()) continue;
            try {
                commandManager.executeCommand(line.split("\\s+"));
                System.out.println();
            }catch (CommandArgumentException e){System.err.println(e.getMessage());
            System.out.println();}
             catch (CommandInterruptedException | NoSuchElementException e){

                if (mode.equals(InputMode.USER_INPUT)){
                    System.err.println("Выполнение команды было прервано. Вы можете продолжать работу. Программа возвращена в безопасное состояние.");
                } else {
                    System.err.println("Команда была пропущена... Обработчик продолжает работу");

                }
            }
        }



































    }
}
