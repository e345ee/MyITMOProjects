package validators.coordinates;


import validators.Validator;

/**
 * Класс валидирующий Y координату.
 */
public class CoordinatesYValidator implements Validator<Integer> {

    /**
     * Валидирует Y координату.
     * @param value Y координата который нужно проверить.
     * @return Валидную Y координату.
     * @throws IllegalArgumentException Если Y координата не валидна.
     */
    public Integer validate(String value) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(getDescription());
        }
        try {
            Integer tmp = Integer.parseInt(value.trim());
            return validate(tmp);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(getDescription());
        }
    }


    /**
     * Валидирует Y координату.
     * @param value Y координата который нужно проверить.
     * @return Валидную Y координату.
     * @throws IllegalArgumentException Если Y координата не валидна.
     */
    @Override
    public Integer validate(Integer value) throws IllegalArgumentException {
        if (value == null){
            throw new IllegalArgumentException(getDescription());
        }
        return value;
    }

    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле y не может быть null и в рамках Integer";
    }
}
