package mods.user;

import exceptions.BuildObjectException;

import mods.ModeManager;
import products.UnitOfMeasure;
import validators.ProductUnitOfMeasureValidator;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для создания объекта класса {@link UnitOfMeasure}
 * Использует ввод с командной строки.
 */
public class UnitOfMeasureUserInputBuilding implements ModeManager<UnitOfMeasure> {

    /**
     * Метод для построения объектов класса {@link UnitOfMeasure}.
     * @return новенький объект класса {@link UnitOfMeasure}.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    @Override
    public UnitOfMeasure buildObject() throws BuildObjectException {
        Scanner scanner = null;
        try {
            System.out.println();
            System.out.println("Генерация UnitOfMeasure...");
            UnitOfMeasure unitOfMeasure;
            scanner = new Scanner(System.in);
            String nextLine;


            ProductUnitOfMeasureValidator unitOfMeasureValidator = new ProductUnitOfMeasureValidator();
            while (true) {
                try {
                    System.out.println("Введите значения UnitOfMeasure:");
                    for (UnitOfMeasure tmp : UnitOfMeasure.values()) {
                        System.out.println("    " + tmp);
                    }
                    nextLine = scanner.nextLine();
                    unitOfMeasure = unitOfMeasureValidator.validate(nextLine);
                    break;


                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный ввод, попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }
            return unitOfMeasure;
        } catch (NoSuchElementException e) {
            throw new BuildObjectException("Во время конструирования объекта произошла ошибка: " + e.getMessage());
        }
    }
}
