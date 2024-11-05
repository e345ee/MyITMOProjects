import managers.*;
import managers.commandManger.CommandExecutor;
import managers.commandManger.InputMode;
import managers.deserializers.CustomProductDeserializer;
import products.Product;


import java.io.IOException;


/**
 * Главный класс для запуска приложения
 * в {@link InputMode#USER_INPUT}.
 * */

public class Main {


    /**
     * Главный метод, который выгружает из файла значения в коллекцию.
     *
     * @param args путь до *json файла, откуда берется значение для загрузки коллекции.
     */
    public static void main(String[] args)  {

        try {
            JsonManager<Product> manager = new JsonManager<>(new FilePathExtractor().getPath(args), new CustomProductDeserializer(), new CustomProductSerializer());
            ProductManager.getInstance().setFileManager(manager);
            ProductManager.getInstance().loadCollection();
        } catch (Exception e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Добро пожаловать в приложение.");
        System.out.println("Используйте help для того, чтобы узнать команды.");



        CommandExecutor executor = new CommandExecutor();
        executor.startExecuting(System.in, InputMode.USER_INPUT);
        System.out.println("Программа приостановлена");


    }
}