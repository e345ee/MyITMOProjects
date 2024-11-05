package models.comporators;

import products.Product;

import java.util.Comparator;

public class NameComparator implements Comparator<Product> {

    /**
     * Сравнивает продукты.
     * @param p1 the first object to be compared.
     * @param p2 the second object to be compared.
     * @return 1 если больше, 0 если равно, -1 если меньше.
     */
    @Override
    public int compare(Product p1, Product p2) {
        if (p1.getName() == null || p2.getName() == null) {
            return 0;
        }

        return p1.getName().compareTo(p2.getName());
    }
}