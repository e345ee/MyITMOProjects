package managers.modes;

import exceptions.BuildObjectException;

/**
 * Интерфейс для построения объекта.
 * @param <T> объект, который мы построили.
 */
public interface Buildingable<T> {

    /**
     * Метод для построения объекта.
     * @return новенький объект.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    T buildObject() throws BuildObjectException;

}
