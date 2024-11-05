package collectionStorageManager;

import com.google.gson.*;
import exceptions.FileCreationException;
import logger.ServerLogger;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Класс для работы с JSON файлами.
 * Читает и записывает в него.
 *
 * @param <E> Класс, объекты которого он будет читать из файла.
 */
public class JsonManager<E> implements FileManager<E> {

    /**
     * Путь до файла.
     */
    String path;

    /**
     * Объект превращающий строки файла в объект {@link #<E>}.
     */
    JsonDeserializer<ArrayList<E>> deserializer;

    /**
     * Объект превращающий объект {@link #<E>} в json представление.
     */
    JsonSerializer<E> serializer;

    /**
     * Объект {@link Scanner} для чтения из файла.
     */
    Scanner scanner;

    /**
     * Конструктор устанавливающий сканнер на чтение из файла.
     * @param path путь до файла.
     * @param deserializer преобразует json в <kE>.
     * @param serializer преобразует <E> в json.
     * @throws AccessDeniedException Если доступ к файлу запрещен.
     * @throws FileNotFoundException Если файл не найден.
     */
    public JsonManager(String path, JsonDeserializer<ArrayList<E>> deserializer, JsonSerializer<E> serializer) throws AccessDeniedException, FileNotFoundException {
        this.path = path;
        this.deserializer = deserializer;
        this.serializer = serializer;

        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден.");
        }
        if (!file.canRead()) throw new AccessDeniedException("Ошибка, не удается открыть файл на пути: " + path);

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Файл не найден.");
        }
    }

    /**
     * Создает файл.
     * @param file файл, который нужно создать.
     * @throws FileCreationException Если не удается создать сканер.
     */
    private void creatFile(File file) throws FileCreationException {
        try {
            System.out.println("Попытка создать новый файл.");
            boolean fileWasMade = file.createNewFile();
            if (!fileWasMade) {
                throw new FileCreationException("Не удалось создать файл.");
            }
            System.out.println("Файл создан.");
        } catch (Exception e) {
            throw new FileCreationException("Не удалось создать файл.");
        }
    }

    /**
     * Возвращает путь к файлу.
     * @return путь к файлу.
     */
    public String getPath() {
        return path;
    }

    /**
     * Закрывает сканер.
     */
    public void closeScanner() {
        scanner.close();
    }

    /**
     * Метод реализует чтение из файла.
     * @return коллекцию ArrayList с <E> объектами.
     */
    @Override
    public ArrayList<E> readFromFile() {
        ArrayList<E> products;
        try {

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(ArrayList.class, deserializer);
            Gson gson = gsonBuilder.create();


            StringBuilder json = new StringBuilder();
            while (scanner.hasNextLine()) {
                json.append(scanner.nextLine());
            }

            if (json.isEmpty()) {
                ServerLogger.logger.error("Файл пустой. Программа будет работать с пустой коллекцией.");
                return new ArrayList<E>();
            }

            products = gson.fromJson(json.toString(), ArrayList.class);


        } catch (RuntimeException e) {
            ServerLogger.logger.error("Ошибка в чтении файла: " + path + "\nПрограмма оперирует с пустой коллекцией.");
            return new ArrayList<E>();
        } finally {
            scanner.close();
        }

        return products;
    }

    /**
     * Записывает в файл ArrayList со значениями <E>
     * @param productList список объектов.
     * @param value Название массива, которое будет отображаться в файле.
     * @param productClass класс объекта, который будут записывать.
     */
    @Override
    public void writeToFile(ArrayList<E> productList, String value, Class<E> productClass) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(productClass, serializer)
                .create();

        JsonObject jsonObject = new JsonObject();
        JsonArray productsArray = new JsonArray();

        for (E product : productList) {
            productsArray.add(gson.toJsonTree(product));
        }

        jsonObject.add(value, productsArray);

        String json = gson.toJson(jsonObject);

        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {

            writer.println(json);
            System.out.println("Коллекция сохранена.");


        } catch (IOException e) {
            System.err.println("Ошибка записи в файл.");
        }
    }

}
