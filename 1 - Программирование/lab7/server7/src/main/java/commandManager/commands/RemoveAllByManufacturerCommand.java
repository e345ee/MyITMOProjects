package commandManager.commands;


import client.ClientHandler;
import collectionStorageManager.PostgresSQLManager;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import products.Organization;
import products.Product;
import responses.CommandStatusResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class RemoveAllByManufacturerCommand implements BaseCommand, ArgumentConsumer<Organization> {
    private static final Logger logger = LogManager.getLogger("RemoveAllByManufacturerCommand");

    private CommandStatusResponse response;
    private ClientHandler client;

    private Organization obj;

    @Override
    public void setObj(Organization obj) {
        this.obj = obj;

        obj.setId(OrganizationIDHandler.generateId());
    }

    @Override
    public String getName() {
        return "remove_all_by_manufacturer";
    }

    @Override
    public String getDescr() {
        return "Удаляет из коллекции все элементы, значение поля manufacturer которого эквивалентно заданному.";
    }

    @Override
    public String getArgs() {
        return "{element}";
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            throw new IllegalArgumentException("Команда " + getName() + " не имеет аргументов.");
        }
        ProductHandler productHandler = ProductHandler.getInstance();
        StringBuilder sb = new StringBuilder();


        PostgresSQLManager dbManager = new PostgresSQLManager();

        for (Product product1 : productHandler.getCollection()){
            if (product1.getManufacturer().equals(obj)){
                int productId = product1.getId();

                if (dbManager.isProductOwnedByUser(productId, client)) {
                    boolean removed = dbManager.removeProductById(productId, client);
                    if (removed) {

                        for (Product product : productHandler.getCollection()) {
                            if (product.getId().equals(productId)) {
                                PartNumberHandler.releasePN(product.getPartNumber());
                            }
                        }
                        productHandler.getCollection().removeIf(product -> Objects.equals(product.getId(), productId));
                        sb.append("Элемент удален. Его ID: ").append(productId).append("\n");;
                    } else {
                        sb.append("Ошибка удаления объекта. Его ID: ").append(productId).append("\n");
                    }
                } else {
                    sb.append("Элемента с таким id нет, или у вас нет прав на его изменение.").append("\n");;
                }
            } else if (sb.isEmpty()){
                sb.append("Элементов с такой организации нет в коллекции.").append("\n");;
            }
        }

        response = CommandStatusResponse.ofString(sb.toString());


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
