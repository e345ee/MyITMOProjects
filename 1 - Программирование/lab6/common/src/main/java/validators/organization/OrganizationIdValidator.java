package validators.organization;



import validators.Validator;

import java.util.InputMismatchException;

/**
 * Класс для валидации id.
 */
public class OrganizationIdValidator implements Validator<Long> {

    /**
     * Валидирует id.
     * @param value id который нужно проверить.
     * @return Валидное id.
     * @throws IllegalArgumentException Если id не валидно.
     */
    public long validate(String value) throws IllegalArgumentException{
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(getDescription());
        }
        try {
            Long tmp = Long.parseLong(value.trim());
            return validate(tmp);
        } catch (InputMismatchException | NumberFormatException e) {
            throw new IllegalArgumentException(getDescription());
        }
    }

    /**
     * Валидирует id.
     * @param value id который нужно проверить.
     * @return Валидное id.
     * @throws IllegalArgumentException Если id не валидно.
     */
    @Override
    public Long validate(Long value) throws IllegalArgumentException {
        if (value == null || value <= 0 ){
            throw new IllegalArgumentException(getDescription());
        } else return value;
    }

    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле id не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, не больше Long.MAX_VALUE";
    }
}
