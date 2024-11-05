package commandManager.commands;


import client.ClientHandler;
import collectionStorageManager.PostgresSQLManager;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import products.Product;
import responses.CommandStatusResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class RemoveByIdCommand implements BaseCommand {
    private static final Logger logger = LogManager.getLogger("RemoveByIdCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescr() {
        return "Удаляет элемент по значению id.";
    }

    @Override
    public String getArgs() {
        return "id";
    }

    @Override
    public void execute(String[] args) {
        if (args.length>2){
            throw new IllegalArgumentException("Команда " + getName()+ " имеет только один аргумент.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();
        Integer productId = Integer.valueOf(args[1]);

        PostgresSQLManager dbManager = new PostgresSQLManager();

        if (dbManager.isProductOwnedByUser(productId, client)) {
            boolean removed = dbManager.removeProductById(productId, client);
            if (removed) {

                for (Product product: productHandler.getCollection()){
                    if (product.getId().equals(productId)){
                        PartNumberHandler.releasePN(product.getPartNumber());
                    }
                }
                productHandler.getCollection().removeIf(product -> Objects.equals(product.getId(), productId));
                response = CommandStatusResponse.ofString("Элемент удален.");
            } else {
                response = CommandStatusResponse.ofString("Ошибка удаления объекта.");
            }
        } else {
            response = CommandStatusResponse.ofString("Элемента с таким id нет, или у вас нет прав на его изменение.");
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