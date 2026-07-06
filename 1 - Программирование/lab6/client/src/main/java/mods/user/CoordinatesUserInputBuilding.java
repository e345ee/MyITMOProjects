package mods.user;

import exceptions.BuildObjectException;

import mods.ModeManager;
import products.Coordinates;
import validators.coordinates.CoordinatesXValidator;
import validators.coordinates.CoordinatesYValidator;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для создания объекта класса {@link Coordinates}
 * Использует ввод с командной строки.
 */
public class CoordinatesUserInputBuilding implements ModeManager<Coordinates> {


    /**
     * Метод для построения объектов класса {@link Coordinates}.
     * @return новенький объект класса {@link Coordinates}.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    @Override
    public Coordinates buildObject() throws BuildObjectException {
        Scanner scanner = null;
        try {System.out.println();
            System.out.println("Генерация координат...");
            Coordinates coordinates = new Coordinates();
            scanner = new Scanner(System.in);
            String nextLine;



            Float x;
            CoordinatesXValidator coordinatesXValidator = new CoordinatesXValidator();
            while (true) {
                try {
                    System.out.println("Введите координату x (type: Float) : ");
                    nextLine = scanner.nextLine();
                    x = coordinatesXValidator.validate(nextLine);
                    coordinates.setX(x);
                    break;
                } catch (IllegalArgumentException | InputMismatchException e) {
                    System.out.println("Неверный ввод, попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }

            Integer y;
            CoordinatesYValidator coordinatesYValidator = new CoordinatesYValidator();
            while (true) {
                try {
                    System.out.println("Введите координату y (type: int) : ");
                    nextLine = scanner.nextLine();
                    y = coordinatesYValidator.validate(nextLine);
                    coordinates.setY(y);
                    break;
                } catch (InputMismatchException | IllegalArgumentException e) {
                    System.out.println("Неверный ввод, попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }
            return coordinates;

        } catch (NumberFormatException | NoSuchElementException e) {
            throw new BuildObjectException("Во время конструирования объекта произошла ошибка: " + e.getMessage());
        }
    }

}
