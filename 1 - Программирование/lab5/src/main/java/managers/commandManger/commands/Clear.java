package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;

/**
 * Класс представляющий реализацию команды очищения коллекции.
 */
public class Clear extends Command {
    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public Clear() {
        super(false);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName(){
        return "clear";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public  String getDescription(){
        return "Очищает коллекцию.";
    }


    /**
     * Очищает коллекцию и списки в менеджерах ведущих учет уникальных значений.
     */
    @Override
    public void execute(){
        if(checkArgument(getArgument())){
            ProductManager.getInstance().clearCollection();
            ProductManager.getInstance().clearUniqueFields();
            System.out.println("Коллекций очищена.");
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
            return true;
        } else {
            System.out.println("У clear нет аргументов!");

            return false;
        }
    }

}
