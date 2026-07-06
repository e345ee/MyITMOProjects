package managers.commandManger.commands;

import exceptions.BuildObjectException;
import managers.ProductManager;
import managers.commandManger.Command;
import managers.modes.Buildingable;
import products.Product;
import products.comparators.PriceComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Класс представляющий реализацию команды добавления объекта, если он меньше минимального в коллекции.
 */
public class AddIfMin extends Command {
    /**
     * Режим, в котором будет работать считывание построения объекта.
     */
    Buildingable<Product> mode;

    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public AddIfMin() {
        super(false);
    }

    /**
     * Задает откуда будет считываться поля для построения объекта.
     * @param mode режим.
     */
    public AddIfMin(Buildingable<Product> mode) {
        super(false);
        this.mode = mode;
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return "add_if_min";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return "Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции.";
    }


    /**
     * Считывает объект и добавляет его, если он меньше минимального в коллекции.
     * @throws BuildObjectException Если произошла ошибка в построении объекта.
     */
    @Override
    public void execute() throws BuildObjectException {
        if (checkArgument(getArgument())) {
            ProductManager manager = ProductManager.getInstance();
            ArrayList<Product> collection = manager.getCollection();
            Product newProduct = mode.buildObject();
            Comparator<Product> comparator = new PriceComparator();

            if (collection.isEmpty()) {
                System.out.println("Коллекция пуста.");
            } else {

                Product mimProduct = Collections.min(collection, comparator);
                if (comparator.compare(newProduct, mimProduct) < 0) {
                    manager.addElementToCollection(newProduct);
                    System.out.println("Объект добавлен в коллекцию.");
                } else {
                    System.out.println("Объект не добавлен.");
                }
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
            System.out.println("Add_if_min не имеет аргументов!");
            return false;
        }
    }
}
