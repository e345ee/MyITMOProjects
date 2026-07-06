package mods.user;

import exceptions.BuildObjectException;

import mods.ModeManager;
import products.Organization;
import utilities.FakeIDGenetator;
import validators.organization.OrganizationEmployeesCountValidator;
import validators.organization.OrganizationNameValidator;

import java.util.NoSuchElementException;
import java.util.Scanner;

//private Long id;
//private String name;
//private long employeesCount;
//private OrganizationType type;
//private Address officialAddress;


/**
 * Класс для создания объекта класса {@link Organization}
 * Использует ввод с командной строки.
 */
public class OrganizationUserInputBuilding implements ModeManager<Organization> {

    /**
     * Метод для построения объектов класса {@link Organization}.
     * @return новенький объект класса {@link Organization}.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    @Override
    public Organization buildObject() throws BuildObjectException {
        Scanner scanner = null;
        try {
            System.out.println();
            System.out.println("Генерация Organization...");
            Organization organization = new Organization();
            scanner = new Scanner(System.in);
            String nextLine;

            organization.setId(Long.valueOf(FakeIDGenetator.generateId()));

            String name;
            OrganizationNameValidator organizationNameValidator = new OrganizationNameValidator();
            while (true) {
                try {
                    System.out.println("Введите имя (type: String) : ");
                    nextLine = scanner.nextLine();

                    name = organizationNameValidator.validate(nextLine);
                    organization.setName(name);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный ввод. Попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }


            long employeesCount;
            OrganizationEmployeesCountValidator organizationEmployeesCountValidator = new OrganizationEmployeesCountValidator();
            while (true) {
                try {
                    System.out.println("Введите employeesCount (type: long) : ");
                    nextLine = scanner.nextLine();

                    employeesCount = organizationEmployeesCountValidator.validate(nextLine);
                    organization.setEmployeesCount(employeesCount);
                    break;

                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный ввод. Попробуйте снова.");
                    System.out.println(e.getMessage());
                }
            }


            OrganizationTypeUserInputBuilding organizationTypeUserInputBuilding = new OrganizationTypeUserInputBuilding();
            organization.setType(organizationTypeUserInputBuilding.buildObject());

            String answer;
            while (true) {
                System.out.println();
                System.out.println("Addres задать null? Yes[Y]/No[N]");
                nextLine = scanner.nextLine();
                if (nextLine.toUpperCase().equals("Y") || nextLine.toUpperCase().equals("N")) {
                    answer = nextLine;
                    break;
                } else {
                    System.out.println("Неверный ввод. Попробуйте снова.");
                }
            }

            if (answer.equals("Y")) {
                System.out.println("Address теперь равно null.");
                organization.setOfficialAddress(null);
            } else {
                AddressUserInputBuilding addressUserInputBuilding = new AddressUserInputBuilding();
                organization.setOfficialAddress(addressUserInputBuilding.buildObject());
            }


            return organization;


        } catch (NoSuchElementException e) {
            throw new BuildObjectException("Во время конструирования объекта произошла ошибка: " + e.getMessage());
        }
    }
}
