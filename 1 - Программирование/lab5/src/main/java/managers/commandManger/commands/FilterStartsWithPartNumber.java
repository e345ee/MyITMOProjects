package managers.commandManger.commands;

import managers.ProductManager;
import managers.commandManger.Command;
import products.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс представляющий реализацию команды вывода объектов, в которых переменная partNumber начинается с аргумента команды.
 */
public class FilterStartsWithPartNumber extends Command {

    /**
     * Базовый конструктор задающий, что аргументов у команды должен быть.
     */
    public FilterStartsWithPartNumber() {
        super(true);
    }

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    @Override
    public String getName(){
        return "filter_starts_with_part_number";
    }

    /**
     * Возвращает описание команды.
     * @return описание команды.
     */
    @Override
    public String getDescription(){
        return "Выводит элементы, значение поля partNumber которых начинается с заданной подстроки.";
    }


    /**
     * Выводит объекты чьи partNumber начинаются с аргумента команды.
     */
    @Override
    public void execute(){
        if (checkArgument(getArgument())) {
            ProductManager manager = ProductManager.getInstance();
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : manager.getCollection()){

                if (product.getPartNumber() == null && this.getArgument() == null) {
                    filteredProducts.add(product);
                } else {
                    if (product.getPartNumber() != null && this.getArgument() != null && product.getPartNumber().startsWith((String) this.getArgument())){
                        filteredProducts.add(product);
                    }

                }
            }

            if (filteredProducts.isEmpty()){
                System.out.println("Таких элементов нет в коллекции.");
            }else {
                int i =1;
                for (Product filteredProduct : filteredProducts){
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
