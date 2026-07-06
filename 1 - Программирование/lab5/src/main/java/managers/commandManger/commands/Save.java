package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;

/**
 * Класс представляющий реализацию команды сохранения коллекции в файл.
 */
public class Save extends Command {

    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public Save() {
        super(false);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return "save";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return "Сохраняет коллекцию в файл.";
    }


    /**
     * Метод сохраняет коллекцию в файл.
     */
    @Override
    public void execute() {
        if (checkArgument(getArgument())) {
            ProductManager manager = ProductManager.getInstance();

            manager.writeCollection();

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
            System.out.println("Save не имеет аргументов!");
            return false;
        }
    }

}
