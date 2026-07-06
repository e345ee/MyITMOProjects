package managers.validators.organization;

import managers.validators.Validator;
import products.OrganizationType;

/**
 * Класс валидирующий type.
 */
public class OrganizationTypeValidator implements Validator<OrganizationType> {

    /**
     * Валидирует type.
     * @param value type который нужно проверить.
     * @return Валидное type.
     * @throws IllegalArgumentException Если type не валидно.
     */
    public OrganizationType validate(String value) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return validate(OrganizationType.valueOf(value.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(getDescription());
        }
    }

    /**
     * Валидирует type.
     * @param value type который нужно проверить.
     * @return Валидное type.
     * @throws IllegalArgumentException Если type не валидно.
     */
    @Override
    public OrganizationType validate(OrganizationType value) throws IllegalArgumentException {
        return value;
    }

    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле type может быть null";
    }
}
