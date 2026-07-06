package managers.commandManger.commands;

import managers.commandManger.Command;
import managers.commandManger.CommandManager;

/**
 * Класс представляющий реализацию команды вывода доступных команд.
 */
public class Help extends Command {
    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public Help() {
        super(false);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName(){
        return "help";
    }
    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription(){
        return "Выводит справку о командах.";
    }

    /**
     * Выводит список доступных команд.
     */
    @Override
    public void execute(){
        if (checkArgument(this.getArgument())){
            CommandManager manager = new CommandManager();
            manager.getCommandMap().forEach((name, command)-> System.out.println(name + " : " + command.getDescription()));
        }
    }

    /**
     * Проверяет переданный аргумент.
     * @param inputArgument аргумент команды.
     * @return возвращает true, если
     */
    @Override
    public boolean checkArgument(Object inputArgument) {
        if (inputArgument == null)
            return true;
        else {
            System.out.println("Help не имеет аргументов!");

            return false;
        }
    }

}
