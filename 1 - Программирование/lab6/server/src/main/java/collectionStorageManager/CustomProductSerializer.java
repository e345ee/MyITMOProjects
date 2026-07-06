package collectionStorageManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import products.Product;

import java.lang.reflect.Type;

/**
 * Класс для превращения объекта класса {@link Product}  в Json представление.
 */
public class CustomProductSerializer implements JsonSerializer<Product> {

    /**
     * Метод превращающий объект в {@link JsonObject}
     * @param product Объект для превращения.
     * @param type тип.
     * @param context контекст.
     * @return представление объекта в форме json.
     */
    @Override
    public JsonObject serialize(Product product, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", product.getId());
        jsonObject.addProperty("name", product.getName());

        // Сериализация Coordinates
        if (product.getCoordinates() != null) {
            jsonObject.add("coordinates", context.serialize(product.getCoordinates()));
        } else {
            jsonObject.addProperty("coordinates", "null");
        }

        // Сериализация LocalDate
        jsonObject.addProperty("creationDate", product.getCreationDate().toString());

        jsonObject.addProperty("price", product.getPrice());
        jsonObject.addProperty("partNumber", product.getPartNumber());

        jsonObject.addProperty("manufactureCost",  product.getManufactureCost() );

        if (product.getUnitOfMeasure() != null) {
            jsonObject.addProperty("unitOfMeasure", product.getUnitOfMeasure().toString());
        } else {
            jsonObject.addProperty("unitOfMeasure", (String) null);
        }
        // Сериализация Organization
        if (product.getManufacturer() != null) {
            jsonObject.add("manufacturer", context.serialize(product.getManufacturer()));
        } else {
            jsonObject.addProperty("manufacturer", "null");
        }

        return jsonObject;
    }

}