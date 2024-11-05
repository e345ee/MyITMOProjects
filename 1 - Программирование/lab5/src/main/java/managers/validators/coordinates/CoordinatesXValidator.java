package managers.validators.coordinates;

import managers.validators.Validator;

/**
 * Класс валидирующий X координату.
 */
public class CoordinatesXValidator implements Validator<Float> {

    /**
     * Валидирует X координату.
     * @param value X координата который нужно проверить.
     * @return Валидную X координату.
     * @throws IllegalArgumentException Если X координата не валидна.
     */
    public Float validate(String value) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException();
        }
        try {
            Float tmp = Float.parseFloat(value.trim());
            return validate(tmp);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(getDescription());
        }
    }


    /**
     * Валидирует X координату.
     * @param value X координата который нужно проверить.
     * @return Валидную X координату.
     * @throws IllegalArgumentException Если X координата не валидна.
     */
    @Override
    public Float validate(Float value) throws IllegalArgumentException {
        if (value == null){throw new IllegalArgumentException(getDescription());}
        return value;
    }


    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле x не может быть null и в рамках Float";
    }
}
