package models.handlers;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class PartNumberHandler {

    /**
     * Список уникальных элементов.
     */
    static Set<String> uniquePartNumbers = new HashSet<>();
    private static final ReentrantLock lock = new ReentrantLock(true);




    /**
     * Высвобождает уникальные значения.
     * @param
     */
    public static void releasePN(String pn) {

        lock.lock();
        try {
            uniquePartNumbers.remove(pn);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверяет наличие в списке.
     * @param value значение partNumber, которое нужно проверить.
     * @return возвращает true, если строка есть, false, если нет.
     */
    public static boolean checkPN(String value) {
        lock.lock();
        try {
            if (value == null) {

                return false;
            }
            return uniquePartNumbers.contains(value);
        } finally {
            lock.unlock();
        }

    }

    /**
     * Добавляет уникальное значение в список.
     * @param value значение для добавления.
     */
    public static void addPN(String value)  {


        lock.lock();
        try {
            if (value == null) {
                uniquePartNumbers.add(null);
            }
            uniquePartNumbers.add(value);
        } finally {
            lock.unlock();
        }



    }
    /**
     * Очищает список уникальных значений.
     */
    public static void clearPN() {
        lock.lock();
        try {
            uniquePartNumbers.clear();
        } finally {
            lock.unlock();
        }

    }

    /**
     * Печатает список.
     */
    public static void print() {
        System.out.println(uniquePartNumbers);
    }
}
