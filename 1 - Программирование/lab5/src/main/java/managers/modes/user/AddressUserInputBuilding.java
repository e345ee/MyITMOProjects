package managers.modes.user;

import exceptions.BuildObjectException;
import managers.modes.Buildingable;
import managers.validators.organization.addess.AddressStreetValidator;
import managers.validators.organization.addess.AddressZipCodeValidator;
import products.Address;
import products.Coordinates;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для создания объекта класса {@link Address}
 * Использует ввод с командной строки.
 */
public class AddressUserInputBuilding implements Buildingable<Address> {

    /**
     * Метод для построения объектов класса {@link Address}.
     * @return новенький объект класса {@link Address}.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    @Override
    public Address buildObject() throws BuildObjectException {
        Scanner scanner = null;
        try {
            System.out.println();
            System.out.println("Генерация адреса");
            scanner = new Scanner(System.in);
            String nextLine;

            Address address = new Address();

            String street;
            AddressStreetValidator addressStreetValidator = new AddressStreetValidator();
            System.out.println("Введите значение street (type: String) :");

            while (true) {
                try {
                    nextLine = scanner.nextLine();
                    street = addressStreetValidator.validate(nextLine);
                    address.setStreet(street);
                    break;
                } catch (IllegalArgumentException | InputMismatchException e) {
                    System.out.println("Неверный ввод, попробуйте снова.");
                    System.out.println(e.getMessage());
                }

            }

            String zipCode;
            AddressZipCodeValidator addressZipCodeValidator = new AddressZipCodeValidator();
            System.out.println("Введите значение zipCode (type: String) :");

            while (true) {
                try {
                    nextLine = scanner.nextLine();
                    zipCode = addressZipCodeValidator.validate(nextLine);
                    address.setZipCode(zipCode);
                    break;
                } catch (IllegalArgumentException | InputMismatchException e) {
                    System.out.println("Неверный ввод, попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }

            return address;


        } catch (NoSuchElementException | NumberFormatException e) {

            throw new BuildObjectException("Во время конструирования объекта произошла ошибка: " + e.getMessage());

        }


    }
}
