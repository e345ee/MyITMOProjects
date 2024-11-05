package commandManager.commands;


import client.ClientHandler;
import collectionStorageManager.PostgresSQLManager;
import models.handlers.ProductHandler;
import products.Product;
import responses.CommandStatusResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FilterByPartNumberCommand  implements BaseCommand{
    private CommandStatusResponse response;
    private ClientHandler client;


    @Override
    public  String getName(){
        return "filter_by_part_number";
    }

    @Override
    public String getDescr() {
        return "Выводит элементы, значение поля partNumber которых равно заданному.";
    }

    @Override
    public void execute(String[] args) {

        if (args.length>2){
            throw new IllegalArgumentException("Команда " + getName()+ " имеет только один аргумент.");
        }

        String[] newArray;
        if (args.length == 1) {
            newArray = new String[args.length + 1];
            System.arraycopy(args, 0, newArray, 0, args.length);
            newArray[newArray.length - 1] = null;
        } else {
            newArray = new String[args.length];
            System.arraycopy(args, 0, newArray, 0, args.length);
        }



        ProductHandler manager = ProductHandler.getInstance();
        PostgresSQLManager postgresSQLManager = new PostgresSQLManager();

        //List<Product> cityList = new ArrayList<>(postgresSQLManager.getCollectionFromDatabase());
       //manager.addMissingCitiesToCollection(cityList);

        // Используем Stream для фильтрации продуктов
        List<Product> filteredProducts = manager.getCollection().stream()
                .filter(product -> {
                    if (product.getPartNumber() == null && newArray[1] == null) {
                        return true;
                    } else {
                        return product.getPartNumber() != null && product.getPartNumber().equals(newArray[1]);
                    }
                })
                .collect(Collectors.toList());

        // Если список пуст, возвращаем соответствующее сообщение
        if (filteredProducts.isEmpty()) {
            response = CommandStatusResponse.ofString("Таких элементов нет в коллекции.");
        } else {

        // Иначе собираем результат в строку
        response = CommandStatusResponse.ofString( IntStream.range(0, filteredProducts.size())
                .mapToObj(i -> String.format("%d) %s", i + 1, filteredProducts.get(i)))
                .collect(Collectors.joining("\n")));}

        //ServerLogger.logger.info(response.getResponse() );
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }

    @Override
    public void setClient(ClientHandler clientManager) {
        this.client = clientManager;
    }
}
