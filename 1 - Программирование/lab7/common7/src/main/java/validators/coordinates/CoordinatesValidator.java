package validators.coordinates;


import products.Coordinates;
import validators.Validator;

/**
 * Класс валидирующий координаты.
 */
public class CoordinatesValidator implements Validator<Coordinates> {

    /**
     * Валидирует координаты.
     * @param value координаты который нужно проверить.
     * @return Валидные координаты.
     * @throws IllegalArgumentException Если координаты не валидны.
     */
    @Override
    public Coordinates validate(Coordinates value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(getDescription());
        }
        try {
            new CoordinatesXValidator().validate(value.getX());
            new CoordinatesYValidator().validate(value.getY());
            return value;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }


    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле coordinates не может быть null";
    }
}
