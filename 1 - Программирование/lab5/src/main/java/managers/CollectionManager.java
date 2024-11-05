package managers;

import java.util.AbstractCollection;

/**
 * Интерфейс для управления коллекцией объектов типа <E>.
 * @param <T> Тип коллекции.
 * @param <E> Тип объекта в коллекции для управления.
 */
public interface CollectionManager<T extends AbstractCollection<E>, E> {

    /**
     * Возвращает коллекцию.
     * @return коллекция
     */
    T getCollection();

    /**
     * Устанавливает коллекцию
     * @param value коллекция, которую нужно установить.
     */
    void setCollection(T value);

    /**
     * Возвращает экземпляр объекта из коллекции.
     * @return Возвращает объект из коллекции.
     */
    E getFirstOrNew();

    /**
     * Добавляет элемент в коллекцию.
     * @param value объект для добавления.
     */
    void addElementToCollection(E value);

    /**
     * Очищает коллекцию.
     */
    void clearCollection();

}


