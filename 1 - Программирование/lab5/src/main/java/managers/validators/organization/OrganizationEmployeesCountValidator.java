package managers.validators.organization;

import managers.validators.Validator;

/**
 * Класс для валидации employeesCount.
 */
public class OrganizationEmployeesCountValidator implements Validator<Long> {

    /**
     * Валидирует employeesCount.
     * @param value employeesCount который нужно проверить.
     * @return Валидное employeesCount.
     * @throws IllegalArgumentException Если employeesCount не валидно.
     */
    public long validate(String value) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(getDescription());
        }
        try {
            Long tmp = Long.parseLong(value.trim());
            return validate(tmp);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(getDescription());
        }
    }

    /**
     * Валидирует employeesCount.
     * @param value employeesCount который нужно проверить.
     * @return Валидное employeesCount.
     * @throws IllegalArgumentException Если employeesCount не валидно.
     */
    @Override
    public Long validate(Long value) throws IllegalArgumentException {
        if (value == null || value <= 0  ) {
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
        return "Поле employeesCount: значение поля должно быть больше 0, меньше Long.MAX_VALUE и не null";
    }
}
