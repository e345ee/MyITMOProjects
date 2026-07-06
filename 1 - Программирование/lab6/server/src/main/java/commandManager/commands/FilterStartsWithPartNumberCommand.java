package commandManager.commands;

import logger.ServerLogger;
import models.handlers.ProductHandler;
import products.Product;
import responses.CommandStatusResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FilterStartsWithPartNumberCommand implements BaseCommand {
    private CommandStatusResponse response;


    @Override
    public String getName() {
        return "filter_starts_with_part_number";
    }

    @Override
    public String getDescr() {
        return "Выводит элементы, значение поля partNumber которых начинается с заданной подстроки.";
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

        // Фильтрация с использованием Stream API
        List<Product> filteredProducts = manager.getCollection().stream()
                .filter(product -> {
                    if (product.getPartNumber() == null && newArray[1] == null) {
                        return true;
                    } else if (product.getPartNumber() != null && newArray[1] != null) {
                        return product.getPartNumber().startsWith((String) newArray[1]);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // Проверка на наличие элементов и вывод результата
        if (filteredProducts.isEmpty()) {
            response = CommandStatusResponse.ofString("Таких элементов нет в коллекции.");
        } else {
            String result = IntStream.range(0, filteredProducts.size())
                    .mapToObj(i -> String.format("%d) %s", i + 1, filteredProducts.get(i)))
                    .collect(Collectors.joining("\n"));

            response = CommandStatusResponse.ofString(result);
        }

        ServerLogger.logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }
}
