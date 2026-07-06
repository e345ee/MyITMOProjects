package commandManager.commands;

import client.ClientHandler;
import collectionStorageManager.PostgresSQLManager;
import models.handlers.PartNumberHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import models.handlers.ProductHandler;
import products.Product;
import responses.CommandStatusResponse;

import java.util.List;

public class ClearCommand implements BaseCommand {
    private static final Logger logger = LogManager.getLogger("ClearCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescr() {
        return "Очищает коллекцию.";
    }

    @Override
    public void execute(String[] args) {

        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();
        PostgresSQLManager postgresSQLManager = new PostgresSQLManager();

        List<Integer> deletedCityIds = postgresSQLManager.clearProductsForUser(client);



        if (!deletedCityIds.isEmpty()) {
            // Освобождение partNumber с использованием stream
            deletedCityIds.forEach(id ->
                    productHandler.getCollection().stream()
                            .filter(product -> product.getId() == id)
                            .forEach(product -> PartNumberHandler.releasePN(product.getPartNumber()))
            );

            // Удаление продуктов из коллекции с использованием stream
            productHandler.getCollection().removeIf(product -> deletedCityIds.contains(product.getId()));

            response = CommandStatusResponse.ofString("Очищено!");

        } else {
            response = CommandStatusResponse.ofString("Ошибка очистки коллекции.");
        }

        logger.info(response.getResponse());

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