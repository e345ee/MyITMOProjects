package managers.ids;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Класс управляющий уникальными значениями id в классе {@link products.Organization}.
 */
public class OrganizationIdManager {
    /**
     * Список уникальных элементов.
     */
    private static final Set<Long> usedIds = new HashSet<>();

    /**
     * Генерирует уникальное значение.
     * @return уникальное значение.
     */
    public static Long generateId() {
        long id = Math.abs(new Random().nextLong());

        while (checkId(id)) {
            id = Math.abs(new Random().nextLong());
        }

        return id;
    }

    /**
     * Очищает список уникальных значений.
     */
    public static void clearIds(){
        usedIds.clear();
    }

    /**
     * Высвобождает уникальные значения.
     * @param id
     */
    public static void releaseId(Long id) {
        usedIds.remove(id);
    }

    /**
     * Проверяет наличие в списке.
     * @param value значение id, которое нужно проверить.
     * @return возвращает true, если строка есть, false, если нет.
     */
    public static boolean checkId(Long value) {
        return usedIds.contains(value);
    }

    /**
     * Добавляет уникальное значение в список.
     * @param value значение для добавления.
     */
    public static void addId(Long value) {
        usedIds.add(value);
    }

    /**
     * Печатает список.
     */
    public static void print(){
        System.out.println(usedIds);
    }

}


