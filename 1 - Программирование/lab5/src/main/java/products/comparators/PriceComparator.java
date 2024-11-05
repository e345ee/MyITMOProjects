package products.comparators;

import products.Product;

import java.util.Comparator;

/**
 * Сравнивает продукты по цене.
 */
public class PriceComparator implements Comparator<Product> {

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

        return Integer.compare(p1.getPrice(), p2.getPrice());
    }
}
