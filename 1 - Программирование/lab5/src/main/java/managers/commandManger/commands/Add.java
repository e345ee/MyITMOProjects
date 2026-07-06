package managers.commandManger.commands;

import exceptions.BuildObjectException;
import managers.ProductManager;
import managers.commandManger.Command;
import managers.modes.Buildingable;
import products.Product;

/**
 * Класс представляющий реализацию команды добавления объекта в коллекцию.
 */
public class Add extends Command {
    /**
     * Режим, в котором будет работать считывание построения объекта.
     */
    Buildingable<Product> mode;

    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public Add() {
        super(false);
    }


    /**
     * Задает откуда будет считываться поля для построения объекта.
     * @param mode режим.
     */
    public Add(Buildingable<Product> mode) {
        super(false);
        this.mode = mode;
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return "add";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return "Добавляет новый элемент в коллекцию.";
    }

    /**
     * Считывает объект и добавляет его в коллекцию.
     * @throws BuildObjectException Если не получилось считать объект.
     */
    @Override
    public void execute() throws BuildObjectException {
        if (checkArgument(this.getArgument())) {
            ProductManager productManager = ProductManager.getInstance();
            productManager.addElementToCollection(mode.buildObject());
            System.out.println("Объект продукт успешно создан.");
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
            System.out.println("У add нет аргументов!");
            return false;
        }
    }

}
