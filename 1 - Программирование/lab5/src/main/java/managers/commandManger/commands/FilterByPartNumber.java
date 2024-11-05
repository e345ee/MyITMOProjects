package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;
import products.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс представляющий реализацию команды вывода объекта, чья partNumber равна аргументу команды.
 */
public class FilterByPartNumber extends Command {

    /**
     * Базовый конструктор задающий, что аргументов у команды должен быть.
     */
    public FilterByPartNumber() {
        super(true);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName() {
        return "filter_by_part_number";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription() {
        return "Выводит элементы, значение поля partNumber которых равно заданному.";
    }

    /**
     * Выводит объект, чья partNumber равна аргументу команды.
     */
    @Override
    public void execute() {
        if (checkArgument(this.getArgument())) {
            ProductManager manager = ProductManager.getInstance();
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : manager.getCollection()) {
                if (product.getPartNumber() == null && this.getArgument() == null) {
                    filteredProducts.add(product);
                } else {
                    if (product.getPartNumber() != null  && product.getPartNumber().equals(this.getArgument())){
                        filteredProducts.add(product);
                    }

                }
            }

            if (filteredProducts.isEmpty()) {
                System.out.println("Таких элементов нет в коллекции.");
            } else {
                int i = 1;
                for (Product filteredProduct : filteredProducts) {
                    System.out.printf("%d) %s \n", i, filteredProduct);
                    i++;
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
        return true;
    }
}
