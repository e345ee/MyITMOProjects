package collectionStorageManager.deserializers;

import com.google.gson.*;
import products.Coordinates;

import java.lang.reflect.Type;

/**
 * Класс для превращения из Json представление в объект класса {@link Coordinates}.
 */
public class CoordinatesDeserializer implements JsonDeserializer<Coordinates> {

    /**
     * Метод для превращения json текста в объект класса {@link Coordinates}.
     * @param json json
     * @param typeOfT type
     * @param context context
     * @return объект класса {@link Coordinates}.
     * @throws JsonParseException Если возникла ошибка парсинга json файла.
     * @throws NumberFormatException Если были неправильно выставлены поля объекта в файле.
     */
    @Override
    public Coordinates deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException, NumberFormatException {
        JsonObject coordinatesObject = json.getAsJsonObject();

        Float x;
        if(coordinatesObject.get("x") != null){x = coordinatesObject.get("x").isJsonNull() ? null :  Float.parseFloat(coordinatesObject.get("x").getAsString());} else {x = null;}




        int y = coordinatesObject.get("y").isJsonNull() ? null :  Integer.parseInt(coordinatesObject.get("y").getAsString());


        return new Coordinates(x, y);
    }
}

