package models.comparators;

import products.Product;

import java.util.Comparator;
import java.util.Objects;

/**
 * Сравнивает продукты по цене. Затем по имени.
 */
public class ProductComparator implements Comparator<Product> {

    /**
     * Сравнивает продукты.
     * @param p1 the first object to be compared.
     * @param p2 the second object to be compared.
     * @return 1 если больше, 0 если равно, -1 если меньше.
     */
    @Override
    public int compare(Product p1, Product p2) {
        if (p1.getPrice() == null || p2.getPrice() == null) {
            return 0;
        }
        if (Objects.equals(p1.getPrice(), p2.getPrice())) {
            return p1.getName().compareTo(p2.getName());
        }
        return Double.compare(p1.getPrice(), p2.getPrice());
    }
}