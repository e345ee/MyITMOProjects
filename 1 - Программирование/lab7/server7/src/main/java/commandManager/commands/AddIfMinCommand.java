package commandManager.commands;


import client.ClientHandler;
import collectionStorageManager.PostgresSQLManager;
import models.comporators.PriceComparator;
import models.handlers.OrganizationIDHandler;
import models.handlers.PartNumberHandler;
import models.handlers.ProductHandler;
import models.handlers.ProductIDHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import products.Product;
import responses.CommandStatusResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AddIfMinCommand implements BaseCommand, ArgumentConsumer<Product> {
    private static final Logger logger = LogManager.getLogger("AddIfMinCommand");


    private CommandStatusResponse response;
    private Product obj;
    private ClientHandler client;

    @Override
    public String getName() {
        return "add_if_min";
    }

    @Override
    public String getDescr() {
        return "Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции.";
    }

    @Override
    public String getArgs() {
        return "{element}";
    }

    @Override
    public void execute(String[] args) {

        if (args.length>1){
            throw new IllegalArgumentException("Команда " + getName()+ " не имеет аргументов.");
        }

        ProductHandler productHandler = ProductHandler.getInstance();



        Comparator<Product> comparator = new PriceComparator();

        if (productHandler.getCollection().isEmpty()) {
            response = CommandStatusResponse.ofString("Коллекция пуста.");
            AddCommand addCommand = new AddCommand();
            addCommand.setObj(obj);
            addCommand.execute(new String[0]);
        } else {
            if (PartNumberHandler.checkPN(obj.getPartNumber())){
                throw new IllegalArgumentException("PartNumber не уникален.");
            }
            Product mimProduct = Collections.min(productHandler.getCollection(), comparator);
            if (comparator.compare(obj, mimProduct) < 0) {




                PostgresSQLManager manager = new PostgresSQLManager();
                ArrayList<Integer> ids = manager.writeObjectToDatabase(obj, client);
                int generatedId =  ids.get(1);

                if (generatedId != -1 && ids.get(0) != -1) {

                    obj.setId(generatedId);
                    obj.getManufacturer().setId(Long.valueOf(ids.get(0)));
                    productHandler.addElementToCollection(obj);
                    response = CommandStatusResponse.ofString("Элемент добавлен с ID: " + generatedId);
                } else response = CommandStatusResponse.ofString("Ошибка добавления элемента.");


            } else {
                response = CommandStatusResponse.ofString("Объект не добавлен. Он не меньше минимального.");
            }
        }




        logger.info(response.getResponse());
    }

    @Override
    public CommandStatusResponse getResponse() {
        return response;
    }

    @Override
    public void setObj(Product obj) {
        this.obj = obj;
        obj.setId(ProductIDHandler.generateId());
        obj.getManufacturer().setId(OrganizationIDHandler.generateId());
    }
    @Override
    public void setClient(ClientHandler clientManager) {
        this.client = clientManager;
    }
}