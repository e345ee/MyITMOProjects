package validators.organization;


import validators.Validator;

/**
 * Класс отвечающий за валидацию имени.
 */
public class OrganizationNameValidator implements Validator<String> {

    /**
     * Валидирует имя.
     * @param value имя который нужно проверить.
     * @return Валидное имя.
     * @throws IllegalArgumentException Если имя не валидно.
     */
    @Override
    public String validate(String value)throws IllegalArgumentException {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(getDescription());
        }

        return value.trim();
    }

    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле name не может быть null, Строка не может быть пустой";
    }
}
