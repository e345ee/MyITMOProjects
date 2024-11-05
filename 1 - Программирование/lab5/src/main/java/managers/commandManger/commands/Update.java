package managers.commandManger.commands;

import exceptions.BuildObjectException;
import managers.ProductManager;
import managers.commandManger.Command;
import managers.modes.Buildingable;
import managers.validators.ProductIdValidator;
import products.Product;

/**
 * Класс команды update. Обновляет поля элемента по его id.
 */
public class Update extends Command {
    /**
     * Режим, в котором будет работать считывание построения объекта.
     */
    Buildingable<Product> mode;

    /**
     * Базовый конструктор задающий, что аргументов у команды должен быть.
     */
    public Update() {
        super(true);
    }

    /**
     * Конструктор задающий существование аргумента и режим, в котором происходит считывание полей объекта.
     * @param mode режим.
     */
    public Update(Buildingable<Product> mode) {
        super(false);
        this.mode = mode;
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return "update";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return "Обновляет значения элемента в коллекции по id.";
    }

    /**
     * Метод запускающий выполнение команды.
     * @throws BuildObjectException Если объект не был построен.
     */
    @Override
    public void execute() throws BuildObjectException {
        if (checkArgument(this.getArgument())) {
            ProductManager manager = ProductManager.getInstance();

            Integer id = new ProductIdValidator().validateUserInput((String) this.getArgument());
            Product oldProduct = manager.getElementById(id);
            Product clone = manager.cloneProduct(oldProduct);

            manager.clearUniqueFieldByProduct(oldProduct);

            manager.getCollection().remove(oldProduct);


            if (clone == null) {
                System.out.println("Такого элемента нет в  коллекции.");
            } else {
                try {
                    Product newProduct =  mode.buildObject();

                    newProduct.setId(id);
                    newProduct.setCreationDate(clone.getCreationDate());
                    manager.addElementToCollection(newProduct);

                    System.out.printf("Поля объекта id:%d обновлены", id);
                    System.out.println();
                } catch (BuildObjectException e){
                    System.out.println("Произошла ошибка, элемент изменения сохранен без изменений.");
                    manager.addElementToCollection(clone);
                    throw e;
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
        if (inputArgument == null) {
            System.out.println("Не предоставлен аргумент!");
            return false;
        }
        return true;
    }

}
