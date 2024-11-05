package managers.validators;

import products.Product;

/**
 * Класс отвечающий за валидацию partNumber.
 */
public class ProductPartNumberValidator implements Validator<String> {

    /**
     * Валидирует partNumber.
     * @param value partNumber который нужно проверить.
     * @return Валидный partNumber.
     * @throws IllegalArgumentException Если partNumber не валиден.
     */
    @Override
    public String validate(String value) throws IllegalArgumentException {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.trim().length() < 20 || value.trim().length() > 69 || Product.PartNumberManager.checkPN(value.trim().replace(" ", "_"))) {
            throw new IllegalArgumentException(getDescription());
        }
        return value.trim().replace(" ", "_");
    }

    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле partNumber: длина строки должна быть не меньше 20, Длина строки не должна быть больше 69, Значение этого поля должно быть уникальным, Поле может быть null";
    }
}
