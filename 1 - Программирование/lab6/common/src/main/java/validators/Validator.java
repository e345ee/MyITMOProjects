package validators;

/**
 * Интерфейс для создания валидаторов.
 * @param <T> объект для валидации.
 */
public interface Validator<T> {

    /**
     * Валидирует значение
     * @param value объект который нужно проверить.
     * @return проверенный и измененный объект.
     * @throws IllegalArgumentException Если объект не был валиден.
     */
    T validate(T value)throws IllegalArgumentException;

    /**
     * Возвращает описание валидатора.
     * @return описание.
     */
    String getDescription();


}