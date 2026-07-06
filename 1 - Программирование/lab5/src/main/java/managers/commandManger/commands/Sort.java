package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;
import products.comparators.ProductComparator;

import java.util.Collections;

/**
 * Класс представляющий реализацию команды сортировки коллекции.
 */
public class Sort extends Command {

    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public Sort() {
        super(false);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return "sort";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return "Сортирует коллекцию.";
    }


    /**
     * Проверяет коллекцию на пустоту. Если коллекция не пустая сортирует ее с помощью {@link ProductComparator}.
     */
    @Override
    public void execute() {
        if (checkArgument(getArgument())) {
            ProductManager manager = ProductManager.getInstance();
            if (manager.getCollection().isEmpty()) {
                System.out.println("Коллекция пуста.");
            } else {
                Collections.sort(manager.getCollection(), new ProductComparator());
                System.out.println("Коллекция отсортирована.");
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
            System.out.println("Sort не имеет аргументов!");
            return false;
        }
    }
}
