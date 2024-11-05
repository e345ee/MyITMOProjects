package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;
import managers.ids.OrganizationIdManager;
import managers.ids.ProductIdManager;
import products.Product;

/**
 * Класс представляющий реализацию команды вывода содержимого коллекции.
 */
public class Show extends Command {

    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public Show(){
        super(false);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName(){
        return "show";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription(){
        return "Выводит список элементов в коллекции.";
    }

    /**
     * Проверяет коллекцию на пустоту. Если она не пуста выводит содержимое коллекции в консоль.
     * Затем выводит списки уникальных полей.
     */
    @Override
    public void execute(){
        if(checkArgument(getArgument())){
            ProductManager manager = ProductManager.getInstance();

            if (manager.getCollection() == null || manager.getCollection().isEmpty()){System.out.println("Нечего показывать.");}
            else {manager.getCollection().forEach(System.out::println);
            System.out.println();
                ProductIdManager.print();
                OrganizationIdManager.print();
                Product.PartNumberManager.print();}
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
            System.out.println("Show не имеет аргументов!");
            return false;

        }
    }
}
