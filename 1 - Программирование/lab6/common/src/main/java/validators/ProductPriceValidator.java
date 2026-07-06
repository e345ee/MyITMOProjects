package validators;


/**
 * Класс отвечающий за валидацию цены.
 */
public class ProductPriceValidator implements Validator<Integer>{

    /**
     * Валидирует цену.
     * @param value объект который нужно проверить.
     * @return Валидная цена.
     * @throws IllegalArgumentException Если цена не валидна.
     */
    public Integer validate(String value) throws IllegalArgumentException{
        if (value == null || value.isBlank()){
            throw new IllegalArgumentException(getDescription());
        }
        try {
            return validate(Integer.parseInt(value.trim()));
        } catch (NumberFormatException e){
            throw new IllegalArgumentException(getDescription());
        }
    }

    /**
     * Валидирует цену.
     * @param value объект который нужно проверить.
     * @return Валидная цена.
     * @throws IllegalArgumentException Если цена не валидна.
     */
    @Override
    public Integer validate(Integer value) throws IllegalArgumentException {
        if(value == null || value <= 0){
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
        return "Поле price не может быть null, Значение поля должно быть больше 0 и в рамках Integer";
    }
}
