package managers.modes.user;

import exceptions.BuildObjectException;
import managers.modes.Buildingable;
import managers.validators.organization.OrganizationTypeValidator;
import products.Organization;
import products.OrganizationType;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для создания объекта класса {@link OrganizationType}
 * Использует ввод с командной строки.
 */
public class OrganizationTypeUserInputBuilding implements Buildingable<OrganizationType> {


    /**
     * Метод для построения объектов класса {@link OrganizationType}.
     * @return новенький объект класса {@link OrganizationType}.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    @Override
    public OrganizationType buildObject() throws BuildObjectException {
        Scanner scanner = null;
        try {
            System.out.println();
            System.out.println("Генерация OrganizationType...");
            OrganizationType organizationType;
            scanner = new Scanner(System.in);
            String nextLine;

            OrganizationTypeValidator organizationTypeValidator = new OrganizationTypeValidator();
            while (true) {
                try {
                    System.out.println("ВВедите значения OrganizationType:");
                    for (OrganizationType tmp : OrganizationType.values()) {
                        System.out.println("    " + tmp);
                    }
                    nextLine = scanner.nextLine();
                    organizationType = organizationTypeValidator.validate(nextLine);
                    break;


                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный ввод, попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }
            return organizationType;
        } catch (NoSuchElementException e) {
            throw new BuildObjectException("Во время конструирования объекта произошла ошибка: " + e.getMessage());
        }
    }
}
