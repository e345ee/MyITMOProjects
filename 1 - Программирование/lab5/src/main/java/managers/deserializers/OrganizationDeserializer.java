package managers.deserializers;

import com.google.gson.*;
import products.Address;
import products.Organization;
import products.OrganizationType;
import products.Product;

import java.lang.reflect.Type;


/**
 * Класс для превращения из Json представление в объект класса {@link Organization}.
 */
public class OrganizationDeserializer implements JsonDeserializer {


    /**
     * Метод для превращения json текста в объект класса {@link Organization}.
     * @param json json
     * @param typeOfT type
     * @param context context
     * @return объект класса {@link Organization}.
     * @throws JsonParseException Если возникла ошибка парсинга json файла.
     * @throws NumberFormatException Если были неправильно выставлены поля объекта в файле.
     */
    @Override
    public Organization deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException, NumberFormatException {
        JsonObject organizationObject = json.getAsJsonObject();

        Long id;
        if (organizationObject.get("id") != null) {
            id = organizationObject.get("id").isJsonNull() ? null : Long.parseLong(organizationObject.get("id").getAsString());
        } else {
            id = null;
        }



        String name;
        if (organizationObject.get("name") != null) {
            name = organizationObject.get("name").isJsonNull() ? null : organizationObject.get("name").getAsString();
        } else {
            name = null;
        }

        long employeesCount;
        if (organizationObject.get("employeesCount")!=null){employeesCount = Long.parseLong(organizationObject.get("employeesCount").getAsString());}else {throw new JsonParseException("Ошибка в парсинге employeesCount.");}


        OrganizationType type;
        if (organizationObject.get("type") != null){type = organizationObject.get("type").isJsonNull() ? null : organizationObject.get("type").getAsString() != null ? OrganizationType.valueOf(organizationObject.get("type").getAsString()) : null;}
        else {type = null;}

        Address officialAddress;
        if (organizationObject.get("officialAddress") != null){officialAddress = organizationObject.get("officialAddress").isJsonNull() ? null: context.deserialize(organizationObject.get("officialAddress"), Address.class);}
        else {officialAddress = null;}

        return new Organization(id, name, employeesCount, type, officialAddress);
    }
}