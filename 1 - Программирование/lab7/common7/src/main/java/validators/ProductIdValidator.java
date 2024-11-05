package validators;



/**
 * Класс для валидации id.
 */
public class ProductIdValidator implements Validator<Integer>{

    /**
     * Валидирует id.
     * @param value id который нужно проверить.
     * @return Валидное id.
     * @throws IllegalArgumentException Если id не валидно.
     */
    public int validate(String value) throws IllegalArgumentException{
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(getDescription());
        }
        try {
            Integer tmp = Integer.parseInt(value.trim());
            return validate(tmp);
        } catch (NumberFormatException e) {
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
    public Integer validate(Integer value) throws IllegalArgumentException {
        if (value == null || value <= 0 ){
            throw new IllegalArgumentException(getDescription());
        }
        return value;
    }
    /**
     * Валидирует ввод с командной строки.
     * @param value id который нужно проверить.
     * @return Валидное id.
     * @throws IllegalArgumentException Если id не валидно.
     */
    public Integer validateUserInput(String value){
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(getDescription());
        }


        try {
            int tmp = Integer.parseInt(value.trim());

            if (tmp <= 0  ){
                throw new IllegalArgumentException("ВВеденное значение меньше нуля.");
            }


            return tmp;


        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Введенное число не Integer");
        }
    }


    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле id не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным. И не больше Integer.";
    }
}
