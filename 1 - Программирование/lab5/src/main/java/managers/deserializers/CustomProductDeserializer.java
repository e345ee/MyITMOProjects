package managers.deserializers;

import com.google.gson.*;
import products.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Класс для превращения из Json представление в объект класса {@link Product}.
 */
public class CustomProductDeserializer implements JsonDeserializer<ArrayList<Product>> {

    /**
     * Метод для превращения json текста в объект класса {@link Product}.
     * @param json json
     * @param typeOfT type
     * @param context context
     * @return объект класса {@link Product}.
     * @throws JsonParseException Если возникла ошибка парсинга json файла.
     */
    @Override
    public ArrayList<Product> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Coordinates.class, new CoordinatesDeserializer())
                .registerTypeAdapter(Organization.class, new OrganizationDeserializer())
                .registerTypeAdapter(Address.class, new AddressDeserializer())
                .create();

        ArrayList<Product> products = new ArrayList<>();
        JsonArray productsArray = json.getAsJsonObject().getAsJsonArray("products");
        for (JsonElement productElement : productsArray) {
            JsonObject productObject = productElement.getAsJsonObject();
            Integer id = null;
            try {

                if (productObject.get("id") != null) {
                    id = productObject.get("id").isJsonNull() ? null : Integer.parseInt(productObject.get("id").getAsString());
                } else {
                    id = null;
                }

                String name;
                if (productObject.get("name") != null) {
                    name = productObject.get("name").isJsonNull() ? null : productObject.get("name").getAsString();
                } else {
                    name = null;
                }

                Coordinates coordinates;
                if (productObject.get("coordinates") != null) {
                    coordinates = productObject.get("coordinates").isJsonNull() ? null : context.deserialize(productObject.get("coordinates"), Coordinates.class);
                } else {
                    coordinates = null;
                }




                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate creationDate;
                if (productObject.get("creationDate") != null) {
                    creationDate = productObject.get("creationDate").isJsonNull() ? null : LocalDate.parse(productObject.get("creationDate").getAsString(), formatter);
                } else {
                    creationDate = null;
                }


                Integer price;
                if (productObject.get("price") != null) {
                    price = productObject.get("price").isJsonNull() ? null : Integer.parseInt(productObject.get("price").getAsString());
                } else {
                    price = null;
                }



                String partNumber;
                if (productObject.get("partNumber") != null) {
                    partNumber = productObject.get("partNumber").isJsonNull() ? null : productObject.get("partNumber").getAsString().replace(" ", "_");
                } else {
                    partNumber = null;
                }

                Float manufactureCost;
                if (productObject.get("manufactureCost") != null) {
                    manufactureCost = productObject.get("manufactureCost").isJsonNull() ? null : Float.parseFloat(productObject.get("manufactureCost").getAsString());
                } else {
                    manufactureCost = null;
                }



                UnitOfMeasure unitOfMeasure;
                if (productObject.get("unitOfMeasure") != null) {
                    unitOfMeasure = productObject.get("unitOfMeasure").isJsonNull() ? null : productObject.get("unitOfMeasure").getAsString() != null ? UnitOfMeasure.valueOf(productObject.get("unitOfMeasure").getAsString()) : null;
                } else {
                    unitOfMeasure = null;
                }

                Organization manufacturer;
                if (productObject.get("manufacturer") != null) {
                    manufacturer = productObject.get("manufacturer").isJsonNull() ? null : context.deserialize(productObject.get("manufacturer"), Organization.class);
                } else {
                    manufacturer = null;
                }




                Product product = new Product(id, name, coordinates, creationDate, price, partNumber, manufactureCost, unitOfMeasure, manufacturer);

                products.add(product);

            } catch (Exception e) {
                System.out.println("Ошибка считывания поля с файла. Пропуск считывания объекта с id: " + id);
                e.printStackTrace();
                continue;
            }

        }
        return products;
    }
}