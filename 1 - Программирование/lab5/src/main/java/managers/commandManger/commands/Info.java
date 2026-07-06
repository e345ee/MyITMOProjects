package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;
import products.Product;

import java.util.Comparator;

/**
 * Класс представляющий реализацию команды вывода информации о коллекции.
 */
public class Info extends Command {

    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public Info() {
        super(false);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName(){
        return "info";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription(){
        return "Выводит информацию о коллекции.";
    }

    /**
     * Выводит тип коллекции, количество элементов и самый старый объект в коллекции.
     */
    @Override
    public void execute() {
        if (checkArgument(getArgument())) {
            ProductManager manager = ProductManager.getInstance();
            if (manager.getCollection() == null || manager.getCollection().isEmpty())
                System.out.println("Коллекция пуста.");
            else {
                System.out.println("Тип коллекции: " + manager.getCollection().getClass().toString());
                System.out.println("Дата инициализации: " + manager.getCollection().stream().min(Comparator.comparing(Product::getCreationDate)).orElse(null).getCreationDate());
                System.out.println("Размер коллекции: " + manager.getCollection().size());
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
        if (inputArgument == null)
            return true;
        else {
            System.out.println("Help не имеет аргументов!");
            return false;
        }
    }

}


