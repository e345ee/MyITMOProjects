package models.handlers;

import products.Product;

import java.util.*;

public class ProductIDHandler {

    /**
     * Список уникальных элементов.
     */
    private static final Set<Integer> usedIds = new HashSet<>();

    /**
     * Генерирует уникальное значение.
     * @return уникальное значение.
     */
    public static Integer generateId() {
        int id = Math.abs(new Random().nextInt());

        while (checkId(id)) {
            id = Math.abs(new Random().nextInt());
        }

        return id;
    }

    /**
     * Высвобождает уникальные значения.
     * @param id
     */
    public static void releaseId(Integer id) {
        usedIds.remove(id);
    }

    /**
     * Проверяет наличие в списке.
     * @param value значение id, которое нужно проверить.
     * @return возвращает true, если строка есть, false, если нет.
     */
    public static boolean checkId(Integer value) {
        return usedIds.contains(value);
    }

    /**
     * Очищает список уникальных значений.
     */
    public static void clearIds(){
        usedIds.clear();
    }

    /**
     * Добавляет уникальное значение в список.
     * @param value значение для добавления.
     */
    public static void addId(Integer value) {
        usedIds.add(value);
    }

    /**
     * Печатает список.
     */
    public static void print(){
        System.out.println(usedIds);
    }



}