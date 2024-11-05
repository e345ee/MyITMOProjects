package validators.organization;


import products.Organization;
import validators.Validator;
import validators.organization.addess.AddressValidator;

/**
 * Класс для валидации организаций.
 */
public class OrganizationValidator implements Validator<Organization> {

    /**
     * Валидирует организацию.
     * @param value организация, которую нужно проверить.
     * @return Валидная организация.
     * @throws IllegalArgumentException Если организация не валидна.
     */
    @Override
    public Organization validate(Organization value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(getDescription());
        }
        try {
            new OrganizationIdValidator().validate(value.getId());
            new OrganizationNameValidator().validate(value.getName());
            new OrganizationEmployeesCountValidator().validate(value.getEmployeesCount());
            new OrganizationTypeValidator().validate(value.getType());
            new AddressValidator().validate(value.getOfficialAddress());
            return value;
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
        return "Поле manufacturer не может быть null";
    }
}
