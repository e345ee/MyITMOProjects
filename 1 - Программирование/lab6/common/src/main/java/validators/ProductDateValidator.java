package validators;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Класс валидирующий LocalDate.
 */
public class ProductDateValidator implements Validator<LocalDate>{

    /**
     * Валидирует LocalDate.
     * @param value LocalDate который нужно проверить.
     * @return Валидное LocalDate.
     * @throws IllegalArgumentException Если LocalDate не валидно.
     */
    public LocalDate validate(String value) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(getDescription());
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(value.trim(), formatter);
            return validate(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(getDescription());
        }
    }

    /**
     * Валидирует LocalDate.
     * @param value LocalDate который нужно проверить.
     * @return Валидное LocalDate.
     * @throws IllegalArgumentException Если LocalDate не валидно.
     */
    @Override
    public LocalDate validate(LocalDate value) throws IllegalArgumentException {
        if (value == null) {
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
        return "Поле creationDate не может быть null";
    }
}
