package managers.modes.nonuser;

import exceptions.BuildObjectException;
import managers.ids.OrganizationIdManager;
import managers.modes.Buildingable;
import managers.validators.organization.OrganizationEmployeesCountValidator;
import managers.validators.organization.OrganizationNameValidator;
import managers.validators.organization.OrganizationTypeValidator;
import managers.validators.organization.OrganizationValidator;
import products.Organization;
import products.Product;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для создания объекта класса {@link Organization}
 * Использует ввод переданный в конструкторе.
 */
public class OrganizationNonUserBuild implements Buildingable<Organization> {
    Scanner scanner;

    /**
     * Конструктор принимающий сканер.
     * @param scanner сканер отвечающий за чтение из источника.
     */
    public OrganizationNonUserBuild(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Метод для построения объектов класса {@link Organization}.
     * @return новенький объект класса {@link Organization}.
     * @throws BuildObjectException Если произошла ошибка при построении.
     */
    @Override
    public Organization buildObject() throws BuildObjectException {
        System.out.println("Генерация Organization...");
        Organization result = new Organization();
        int valuesToRead = 4;
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
            Long id = OrganizationIdManager.generateId();
            result.setId(id);
            System.out.println("Значение id установлено. Id = "+ id);

            //name
            System.out.println("Попытка установить name = " + values.get(0));
            result.setName(new OrganizationNameValidator().validate(values.get(0)));
            System.out.println("Значение name установлено.");

            //employeesCount
            System.out.println("Попытка установить employeesCount = " + values.get(1));
            result.setEmployeesCount(new OrganizationEmployeesCountValidator().validate(values.get(1)));
            System.out.println("Значение employeesCount установлено.");

            //type
            System.out.println("Попытка установить type = " + values.get(2));
            result.setType(new OrganizationTypeValidator().validate(values.get(2)));
            System.out.println("Значение type установлено.");

            //Y/N
            System.out.println("Значение Y/N равно:" + values.get(3));
            switch (values.get(3).toUpperCase()) {
                case "Y":
                    result.setOfficialAddress(new AddressNonUserBuild(scanner).buildObject());
                    break;
                case "N":
                    result.setOfficialAddress(null);
                    break;
                default:
                    throw new IllegalArgumentException("Неверно указан литерал отвечающий за построение адреса.");
            }


            return new OrganizationValidator().validate(result);

        } catch (IllegalArgumentException | NullPointerException | NoSuchElementException e) {
            throw new BuildObjectException("Предоставленные данные для построения объекта неверны. Воспользуйтесь ручным вводом или исправьте команду в скрипте.\n" + e.getMessage());
        }


    }
}
