package managers.validators.organization.addess;

import managers.validators.Validator;
import products.Address;

/**
 * Класс для валидации адреса.
 */
public class AddressValidator implements Validator<Address> {


    /**
     * Валидирует адрес.
     * @param address адрес который нужно проверить.
     * @return Валидный адрес.
     * @throws IllegalArgumentException Если адрес не валидно.
     */
    @Override
    public Address validate(Address address) throws IllegalArgumentException {
        if (address == null) {
            return null;
        }
        try {
            new AddressStreetValidator().validate(address.getStreet());
            new AddressZipCodeValidator().validate(address.getZipCode());
            return address;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }


    /**
     * Метод возвращает описание.
     * @return описание.
     */
    @Override
    public String getDescription() {
        return "Поле может быть null";
    }


}
