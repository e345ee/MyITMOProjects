package collectionStorageManager.deserializers;

import com.google.gson.*;
import products.Address;

import java.lang.reflect.Type;

/**
 * Класс для превращения из Json представление в объект класса {@link Address}.
 */
public class AddressDeserializer implements JsonDeserializer<Address> {


    /**
     * Метод для превращения json текста в объект класса {@link Address}.
     * @param json json
     * @param typeOfT type
     * @param context context
     * @return объект класса {@link Address}.
     * @throws JsonParseException Если возникла ошибка парсинга json файла.
     * @throws NumberFormatException Если были неправильно выставлены поля объекта в файле.
     */
    @Override
    public Address deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject addressObject = json.getAsJsonObject();

        String street;
        if (addressObject.get("street") != null) {
            street = addressObject.get("street").isJsonNull() ? null : addressObject.get("street").getAsString().replace(" ", "_");
        } else {
            street = null;
        }

        String zipCode;
        if (addressObject.get("zipCode") != null) {
            zipCode = addressObject.get("zipCode").isJsonNull() ? null : addressObject.get("zipCode").getAsString().replace(" ", "_");
        } else {
            zipCode = null;
        }




        return new Address(street, zipCode);
    }
}



