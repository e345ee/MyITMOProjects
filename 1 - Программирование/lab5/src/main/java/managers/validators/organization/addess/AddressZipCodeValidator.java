package managers.validators.organization.addess;

import managers.validators.Validator;

/**
 * Класс для валидации zipCode.
 */
public class AddressZipCodeValidator implements Validator<String> {

    /**
     * Валидирует zipCode.
     * @param value zipCode который нужно проверить.
     * @return Валидное zipCode.
     * @throws IllegalArgumentException Если zipCode не валидно.
     */
    @Override
    public String validate(String value) throws IllegalArgumentException {

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(getDescription());
        }


        if ((value.trim().length() > 14 && !value.isBlank()) || (value.length() > 14 && value.isBlank())) {
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
        return "Поле ZipCode: длина строки не должна быть больше 14, Поле не может быть null";
    }
}
