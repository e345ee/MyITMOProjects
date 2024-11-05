package managers.validators.organization.addess;

import managers.validators.Validator;

/**
 * Класс для валидации street.
 */
public class AddressStreetValidator implements Validator<String> {

    /**
     * Валидирует street.
     * @param value street который нужно проверить.
     * @return Валидное street.
     * @throws IllegalArgumentException Если street не валидно.
     */
    @Override
    public String validate(String value) throws IllegalArgumentException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        if ((value.trim().length() > 148 && !value.isBlank()) || (value.length() > 148 && value.isBlank())) {
            throw new IllegalArgumentException(getDescription());
        }

        if (value.isBlank()) {
            return value;
        } else {
            return value.trim();
        }
    }


    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле street: длина строки не должна быть больше 148, Поле может быть null";
    }


}
