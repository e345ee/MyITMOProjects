package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;
import managers.validators.ProductIdValidator;
import products.Product;

/**
 * Класс представляющий реализацию команды удаления объекта по его id.
 */
public class RemoveById extends Command {

    /**
     * Базовый конструктор задающий, что аргументов у команды должен быть.
     */
    public RemoveById() {
        super(true);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return "remove_by_id";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return "Удаляет элемент по значению id.";
    }

    /**
     * Метод проверяет наличие элемента в коллекции,
     * если он найден то, сначала удаляются его уникальные значения из менеджеров по их учёту.
     * Затем удаляется сам объект.
     */
    @Override
    public void execute(){
        if(checkArgument(this.getArgument())){

            Integer id = new ProductIdValidator().validateUserInput((String) this.getArgument());

            Product product= ProductManager.getInstance().getElementById(id);
            if (product == null){
                System.out.println("Такого элемента нет в  коллекции.");
            } else {
                System.out.println("Удаление объекта под id: " + id);
                ProductManager.getInstance().clearUniqueFieldByProduct(product);
                ProductManager.getInstance().getCollection().remove(product);
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
