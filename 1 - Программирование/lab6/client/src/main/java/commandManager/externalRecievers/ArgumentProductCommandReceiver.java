package commandManager.externalRecievers;

import commandLogic.CommandDescription;
import commandLogic.commandReceiverLogic.receivers.ExternalArgumentReceiver;
import exceptions.BuildObjectException;
import mods.ModeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import products.Product;
import request.ArgumentRequestSender;
import responses.CommandStatusResponse;
import server.ServerConnectionHandler;

//Обрабатывает команды, которые требуют объекта Product в качестве аргумента.
//Использует ModeManager<Product> для создания объекта Product в зависимости от режима работы (ввод от пользователя или через скрипт).
//После создания объекта Product отправляет команду и объект на сервер через ArgumentRequestSender.
public class ArgumentProductCommandReceiver implements ExternalArgumentReceiver<Product> {

    private static final Logger logger = LogManager.getLogger("ArgumentCityCommandReceiver");
    ModeManager<Product> modeManager;
    Product product;

    {
        product = new Product();
    }

    public ArgumentProductCommandReceiver(ModeManager<Product> modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public boolean receiveCommand(CommandDescription command, String[] args) throws BuildObjectException {
        product = modeManager.buildObject();
        CommandStatusResponse response = new ArgumentRequestSender<Product>().sendCommand(command, args, product, ServerConnectionHandler.getCurrentConnection());
        if (response != null) {
            logger.info("Status code: " + response.getStatusCode());
            logger.info("Response: \n" + response.getResponse());
            return true;
        }
        return false;
    }

    @Override
    public Product getArguemnt() {
        return product;
    }
}