package validators;


import products.Product;
import validators.coordinates.CoordinatesValidator;
import validators.organization.OrganizationValidator;

/**
 * Валидатор для проверки продуктов.
 */
public class ProductValidator implements Validator<Product>{

    /**
     * Валидирует продукты.
     * @param value объект который нужно проверить.
     * @return Валидный продукт.
     * @throws IllegalArgumentException Если продукт не валиден.
     */
    @Override
    public Product validate(Product value) throws IllegalArgumentException{
        if (value == null){
            throw  new IllegalArgumentException(getDescription());
        }

        try {
            new ProductIdValidator().validate(value.getId());
            new ProductNameValidator().validate(value.getName());
            new CoordinatesValidator().validate(value.getCoordinates());
            new ProductDateValidator().validate(value.getCreationDate());
            new ProductPriceValidator().validate(value.getPrice());
            new ProductPartNumberValidator().validate(value.getPartNumber());
            new ProductManufactureCostValidator().validate(value.getManufactureCost());
            new ProductUnitOfMeasureValidator().validate(value.getUnitOfMeasure());
            new OrganizationValidator().validate(value.getManufacturer());

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
        return "Не может быть null";
    }


}
