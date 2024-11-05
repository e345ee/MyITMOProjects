package mods.nonuser;

import exceptions.BuildObjectException;

import mods.ModeManager;
import products.Coordinates;
import products.Product;
import utilities.FakeIDGenetator;
import validators.*;
import validators.coordinates.CoordinatesValidator;
import validators.coordinates.CoordinatesXValidator;
import validators.coordinates.CoordinatesYValidator;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для создания объекта класса {@link Product}
 * Использует ввод переданный в конструкторе.
 */
public class ProductNonUserBuild implements ModeManager<Product> {

    Scanner scanner;

    /**
     * Конструктор принимающий сканер.
     * @param scanner сканер отвечающий за чтение из источника.
     */
    public ProductNonUserBuild(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Метод для построения объектов класса {@link Product}.
     * @return новенький объект класса {@link Product}.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    @Override
    public Product buildObject() throws BuildObjectException {
        System.out.println("Генерация Product...");
        Product result = new Product();
        int valuesToRead = 7;
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
            //id
            Integer id = FakeIDGenetator.generateId();
            result.setId(id);
            System.out.println("Значение id установлено. Id = "+ id);

            //name
            System.out.println("Попытка установить name = " + values.get(0));
            result.setName(new ProductNameValidator().validate(values.get(0)));
            System.out.println("Значение name установлено.");

            //Coordinates
            System.out.println("Создание координат.");
            Coordinates coordinates = new Coordinates();

            System.out.println("Попытка установить x = " + values.get(1));
            coordinates.setX(new CoordinatesXValidator().validate(values.get(1)));
            System.out.println("Значение x установлено.");

            System.out.println("Попытка установить y = " + values.get(2));
            coordinates.setY(new CoordinatesYValidator().validate(values.get(2)));
            System.out.println("Значение y установлено.");

            result.setCoordinates(new CoordinatesValidator().validate(coordinates));

            //Date
            LocalDate creationDate = Date.valueOf(LocalDate.now()).toLocalDate();
            result.setCreationDate(creationDate);
            System.out.println("Значение Date установлено. Date = "+ creationDate);

            //price
            System.out.println("Попытка установить price = " + values.get(3));
            result.setPrice(new ProductPriceValidator().validate(values.get(3)));
            System.out.println("Значение price установлено.");

            //partNumber
            System.out.println("Попытка установить partNumber = " + values.get(4));
            result.setPartNumber(new ProductPartNumberValidator().validate(values.get(4)));
            System.out.println("Значение partNumber установлено.");

            //manufactureCost
            System.out.println("Попытка установить manufactureCost = " + values.get(5));
            result.setManufactureCost(new ProductManufactureCostValidator().validate(values.get(5)));
            System.out.println("Значение manufactureCost установлено.");

            //unitOfMeasure
            System.out.println("Попытка установить unitOfMeasure = " + values.get(6));
            result.setManufactureCost(new ProductManufactureCostValidator().validate(values.get(6)));
            System.out.println("Значение unitOfMeasure установлено.");

            //manufacturer
            result.setManufacturer(new OrganizationNonUserBuild(scanner).buildObject());

            return new ProductValidator().validate(result);

        } catch (IllegalArgumentException | NullPointerException | NoSuchElementException e) {
            throw new BuildObjectException("Предоставленные данные для построения объекта неверны. Воспользуйтесь ручным вводом или исправьте команду в скрипте.\n" + e.getMessage());
        }


    }
}
