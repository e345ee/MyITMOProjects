package managers.validators;

import products.UnitOfMeasure;

/**
 * Класс для валидации {@link UnitOfMeasure}.
 */
public class ProductUnitOfMeasureValidator implements Validator<UnitOfMeasure>{

    /**
     * Валидирует {@link UnitOfMeasure}.
     * @param value строка который нужно проверить.
     * @return Валидный {@link UnitOfMeasure}.
     * @throws IllegalArgumentException Если {@link UnitOfMeasure} не валиден.
     */
    public UnitOfMeasure validate(String value) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return validate(UnitOfMeasure.valueOf(value.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(getDescription());
        }
    }

    /**
     * Валидирует {@link UnitOfMeasure}.
     * @param value объект который нужно проверить.
     * @return Валидный {@link UnitOfMeasure}.
     * @throws IllegalArgumentException Если {@link UnitOfMeasure} не валиден.
     */
    @Override
    public UnitOfMeasure validate(UnitOfMeasure value) throws IllegalArgumentException {
        return value;
    }

    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле unitOfMeasure может быть null";
    }
}
