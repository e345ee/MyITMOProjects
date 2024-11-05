package models.handlers;

import java.util.HashSet;
import java.util.Set;

public class PartNumberHandler {

    /**
     * Список уникальных элементов.
     */
    static Set<String> uniquePartNumbers = new HashSet<>();

    /**
     * Высвобождает уникальные значения.
     * @param pn
     */
    public static void releasePN(String pn) {
        uniquePartNumbers.remove(pn);
    }

    /**
     * Проверяет наличие в списке.
     * @param value значение partNumber, которое нужно проверить.
     * @return возвращает true, если строка есть, false, если нет.
     */
    public static boolean checkPN(String value) {
        if (value == null) {
            return false;
        }
        return uniquePartNumbers.contains(value);
    }

    /**
     * Добавляет уникальное значение в список.
     * @param value значение для добавления.
     */
    public static void addPN(String value)  {

        if (value == null) {
            uniquePartNumbers.add(null);
        }
        uniquePartNumbers.add(value);

    }
    /**
     * Очищает список уникальных значений.
     */
    public static void clearPN() {
        uniquePartNumbers.clear();
    }

    /**
     * Печатает список.
     */
    public static void print() {
        System.out.println(uniquePartNumbers);
    }
}
