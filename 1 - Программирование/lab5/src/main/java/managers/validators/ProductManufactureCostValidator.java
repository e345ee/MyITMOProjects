package managers.validators;

/**
 * Класс для валидации ManufactureCost.
 */
public class ProductManufactureCostValidator implements  Validator<Float>{

    /**
     * Валидирует ManufactureCost.
     * @param value ManufactureCost который нужно проверить.
     * @return Валидное ManufactureCost.
     * @throws IllegalArgumentException Если ManufactureCost не валидно.
     */
    public Float validate(String value) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Float tmp = Float.parseFloat(value.trim());
            return validate(tmp);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(getDescription());
        }
    }


    /**
     * Валидирует ManufactureCost.
     * @param value ManufactureCost который нужно проверить.
     * @return Валидное ManufactureCost.
     * @throws IllegalArgumentException Если ManufactureCost не валидно.
     */
    @Override
    public Float validate(Float value) throws IllegalArgumentException {
        return value;
    }

    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле manufactureCost может быть null и в рамках Float";
    }
}
