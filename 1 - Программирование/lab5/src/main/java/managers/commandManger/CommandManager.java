package managers.commandManger;

import exceptions.BuildObjectException;
import exceptions.CommandArgumentException;
import exceptions.CommandInterruptedException;
import exceptions.UnknownCommandException;
import managers.commandManger.commands.*;
import managers.modes.Buildingable;
import managers.modes.nonuser.OrganizationNonUserBuild;
import managers.modes.nonuser.ProductNonUserBuild;
import managers.modes.user.OrganizationUserInputBuilding;
import managers.modes.user.ProductUserInputBuilding;
import products.Organization;
import products.Product;

import java.util.*;

/**
 * Класс для работы с командами.
 * Имеет список доступных для исполнения команд.
 * Запускает команду в работу.
 */
public class CommandManager {

    /**
     * Список доступных для вызова команд.
     */
    private LinkedHashMap<String, Command> commandMap;


    /**
     * Конструктор инициализирующий доступные команды.
     * @param mode В каком режиме команды будут работать.
     * @param scanner Источник чтения команд.
     */
    public CommandManager(InputMode mode, Scanner scanner) {
        commandMap = new LinkedHashMap<>();

        Buildingable<Product> generatorP = null;
        Buildingable<Organization> generatorO = null;

        commandMap.put("show", new Show());
        commandMap.put("help", new Help());
        commandMap.put("info", new Info());
        commandMap.put("clear", new Clear());
        commandMap.put("remove_by_id", new RemoveById());
        commandMap.put("history", new History());
        commandMap.put("save", new Save());
        commandMap.put("filter_by_part_number", new FilterByPartNumber());
        commandMap.put("filter_starts_with_part_number", new FilterStartsWithPartNumber());
        commandMap.put("sort", new Sort());
        commandMap.put("execute_script", new ExecuteScript());
        commandMap.put("exit", new Exit(mode));


        switch (mode) {
            case USER_INPUT -> {
                generatorP = new ProductUserInputBuilding();
                generatorO = new OrganizationUserInputBuilding();
            }
            case NON_USER_INPUT -> {
                generatorP = new ProductNonUserBuild(scanner);
                generatorO = new OrganizationNonUserBuild(scanner);
            }
        }
        commandMap.put("update_by_id", new Update(generatorP));
        commandMap.put("add", new Add(generatorP));
        commandMap.put("remove_all_by_manufacturer", new RemoveAllByManufacturer(generatorO));
        commandMap.put("add_if_min", new AddIfMin(generatorP));
    }


    public CommandManager() {
        commandMap = new LinkedHashMap<>();



        commandMap.put("show", new Show());
        commandMap.put("help", new Help());
        commandMap.put("info", new Info());
        commandMap.put("clear", new Clear());
        commandMap.put("remove_by_id", new RemoveById());
        commandMap.put("history", new History());
        commandMap.put("save", new Save());
        commandMap.put("filter_by_part_number", new FilterByPartNumber());
        commandMap.put("filter_starts_with_part_number", new FilterStartsWithPartNumber());
        commandMap.put("sort", new Sort());
        commandMap.put("execute_script", new ExecuteScript());
        commandMap.put("exit", new Exit());


        commandMap.put("update_by_id", new Update());
        commandMap.put("add", new Add());
        commandMap.put("remove_all_by_manufacturer", new RemoveAllByManufacturer());
        commandMap.put("add_if_min", new AddIfMin());
    }

    /**
     * Метод для получения всех команд.
     * @return список команд.
     */
    public HashMap<String, Command> getCommandMap() {
        return commandMap;
    }


    /**
     * Метод для вызова команды.
     * @param args Строка содержащая команду и аргументы.
     * @throws CommandInterruptedException Если выполнение команды было прервано.
     * @throws CommandArgumentException Если аргументы были заданы неправильно.
     */
    public void executeCommand(String[] args) throws CommandInterruptedException, CommandArgumentException {

        try {

            Command command = null;
            command = Optional.ofNullable(commandMap.get(args[0])).orElseThrow(() -> new UnknownCommandException("Команды " + args[0] + " не обнаружено."));
            if (args.length > 1) {
                if (args.length > 2) {
                    throw new IllegalArgumentException("Некорректно заданы аргументы");

                }
                command.setArgument(args[1]);
            }
            History.addToHistory(command.getName());
            command.execute();
            command.setArgument(null);
            

        } catch (IllegalArgumentException | NullPointerException | NoSuchElementException e) {
            e.printStackTrace();
            throw new CommandArgumentException("Выполнение команды пропущено из-за неправильных предоставленных аргументов! (" + e.getMessage() + ")");
        } catch (UnknownCommandException e) {
            System.err.println(e.getMessage());
        } catch (BuildObjectException e) {
            System.err.println(e.getMessage());
            throw new CommandInterruptedException(e);
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка!");
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new CommandInterruptedException(e);
        }
    }
}


