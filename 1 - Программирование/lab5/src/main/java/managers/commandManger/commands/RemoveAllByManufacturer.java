package managers.commandManger.commands;

import exceptions.BuildObjectException;
import managers.ProductManager;
import managers.commandManger.Command;
import managers.modes.Buildingable;
import products.Organization;
import products.Product;

import java.util.Iterator;

/**
 * Класс представляющий реализацию команды удаления объекта по значению его мануфактуры.
 */
public class RemoveAllByManufacturer extends Command {
    /**
     * Режим, в котором будет работать считывание построения объекта.
     */
    Buildingable<Organization> mode;

    /**
     * Базовый конструктор задающий, что аргументов у команды быть не должно.
     */
    public RemoveAllByManufacturer() {
        super(false);
    }

    /**
     * Задает откуда будет считываться поля для построения объекта.
     * @param mode режим.
     */
    public RemoveAllByManufacturer(Buildingable<Organization> mode) {
        super(false);
        this.mode = mode;
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName(){
        return "remove_all_by_manufacturer";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription(){
        return "Удаляет из коллекции все элементы, значение поля manufacturer которого эквивалентно заданному.";
    }

    /**
     * Метод начинает считывать построение объекта {@link Organization} , затем проверяет есть ли такой продукт,
     * в котором организация равна тому, что мы заранее сконструировали.
     * @throws BuildObjectException Если организация не была сконструирована.
     */
    @Override
    public void execute() throws BuildObjectException {
        if (checkArgument(this.getArgument())) {
            ProductManager productManager = ProductManager.getInstance();
            Organization organization = mode.buildObject();
            boolean flag = false;



            Iterator<Product> iterator = productManager.getCollection().iterator();
            while(iterator.hasNext()) {
                Product element = iterator.next();
                if(element.getManufacturer().equals(organization)) {
                    System.out.printf("\nОбъект id:%d удалён.\n",element.getId());
                    iterator.remove();
                    productManager.clearUniqueFieldByProduct(element);
                    flag = true;
                }
            }
            if (!flag){System.out.println("Равных мануфактур не найдено. Ничего не удалено.");}
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
            System.out.println("remove_all_by_manufacturer не имеет аргументов!");
            return false;
        }
    }
}
