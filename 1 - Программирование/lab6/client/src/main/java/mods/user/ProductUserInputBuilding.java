package mods.user;

import exceptions.BuildObjectException;

import mods.ModeManager;
import products.Product;
import utilities.FakeIDGenetator;
import validators.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
public class Product {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer price; //Поле не может быть null, Значение поля должно быть больше 0
    private String partNumber; //Длина строки должна быть не меньше 20, Длина строки не должна быть больше 69, Значение этого поля должно быть уникальным, Поле может быть null
    private Float manufactureCost; //Поле может быть null
    private UnitOfMeasure unitOfMeasure; //Поле может быть null
    private Organization manufacturer; //Поле не может быть null
}
 */

/**
 * Класс для создания объекта класса {@link Product}
 * Использует ввод с командной строки.
 */
public class ProductUserInputBuilding implements ModeManager<Product> {

    /**
     * Метод для построения объектов класса {@link Product}.
     * @return новенький объект класса {@link Product}.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    @Override
    public Product buildObject() throws BuildObjectException {
        Scanner scanner = null;
        try {
            System.out.println("Генерация Product...");
            Product product = new Product();
            scanner = new Scanner(System.in);
            String nextLine;

            product.setId(FakeIDGenetator.generateId());

            String name;
            ProductNameValidator productNameValidator = new ProductNameValidator();
            while (true) {
                try {
                    System.out.println("Введите name (type: String) : ");
                    nextLine = scanner.nextLine();
                    name = productNameValidator.validate(nextLine);
                    product.setName(name);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный ввод. Попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }

            CoordinatesUserInputBuilding coordinatesUserInputBuilding = new CoordinatesUserInputBuilding();
            product.setCoordinates(coordinatesUserInputBuilding.buildObject());


            LocalDate creationDate = Date.valueOf(LocalDate.now()).toLocalDate();
            product.setCreationDate(creationDate);


            Integer price;
            ProductPriceValidator productPriceValidator = new ProductPriceValidator();
            while (true) {
                try {
                    System.out.println("Введите price (type: Integer) : ");
                    nextLine = scanner.nextLine();
                    price = productPriceValidator.validate(nextLine);
                    product.setPrice(price);
                    break;

                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный ввод. Попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }


            String partNumber;
            ProductPartNumberValidator productPartNumberValidator = new ProductPartNumberValidator();
            while (true) {
                try {
                    System.out.println("Введите partNumber (type: String) : ");
                    nextLine = scanner.nextLine();
                    partNumber = productPartNumberValidator.validate(nextLine);
                    product.setPartNumber(partNumber);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный ввод. Попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }


            Float manufactureCost;
            ProductManufactureCostValidator productManufactureCostValidator = new ProductManufactureCostValidator();
            while (true) {
                try {
                    System.out.println("Введите manufactureCost (type: Float) : ");
                    nextLine = scanner.nextLine();
                    manufactureCost = productManufactureCostValidator.validate(nextLine);
                    product.setManufactureCost(manufactureCost);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный ввод. Попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }

            UnitOfMeasureUserInputBuilding unitOfMeasureUserInputBuilding = new UnitOfMeasureUserInputBuilding();
            product.setUnitOfMeasure(unitOfMeasureUserInputBuilding.buildObject());


            OrganizationUserInputBuilding organizationUserInputBuilding = new OrganizationUserInputBuilding();
            product.setManufacturer(organizationUserInputBuilding.buildObject());


            return new ProductValidator().validate(product);
        } catch (NoSuchElementException e) {
            throw new BuildObjectException("Во время конструирования объекта произошла ошибка: " + e.getMessage());
        }
    }
}
