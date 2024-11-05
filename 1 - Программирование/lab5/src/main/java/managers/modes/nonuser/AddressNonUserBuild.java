package managers.modes.nonuser;

import exceptions.BuildObjectException;
import managers.modes.Buildingable;
import managers.validators.organization.addess.AddressStreetValidator;
import managers.validators.organization.addess.AddressValidator;
import managers.validators.organization.addess.AddressZipCodeValidator;
import products.Address;
import products.Organization;
import products.Product;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для создания объекта класса {@link Address}
 * Использует ввод переданный в конструкторе.
 */
public class AddressNonUserBuild implements Buildingable<Address> {

    Scanner scanner;

    /**
     * Конструктор принимающий сканер.
     * @param scanner сканер отвечающий за чтение из источника.
     */
    public AddressNonUserBuild(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Метод для построения объектов класса {@link Address}.
     * @return новенький объект класса {@link Address}.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    @Override
    public Address buildObject() throws BuildObjectException {


        System.out.println("Генерация Address...");
        Address result = new Address();
        int valuesToRead = 2;
        ArrayList<String> values = new ArrayList<>(valuesToRead);

        for (int i = 0; i < valuesToRead && scanner.hasNextLine(); i++) {
            String line = scanner.nextLine();
            if (!line.isEmpty()) {
                values.add(line);
            } else {
                values.add(null);
            }
        }
        try {

            //street
            System.out.println("Попытка установить street = " + values.get(0));
            result.setStreet(new AddressStreetValidator().validate(values.get(0)));
            System.out.println("Значение street установлено.");


            //zipCode
            System.out.println("Попытка установить zipCode = " + values.get(1));
            result.setZipCode(new AddressZipCodeValidator().validate(values.get(1)));
            System.out.println("Значение zipCode установлено.");




            return new AddressValidator().validate(result);

        } catch (IllegalArgumentException | NullPointerException | NoSuchElementException e) {
            throw new BuildObjectException("Предоставленные данные для построения объекта неверны. Воспользуйтесь ручным вводом или исправьте команду в скрипте.\n" + e.getMessage());
        }


    }
}
