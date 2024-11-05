package commandManager.externalRecievers;

import products.Product;
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

public class ArgumentPCommandReceiver implements ExternalArgumentReceiver<Product> {

    private static final Logger logger = LogManager.getLogger("ArgumentPCommandReceiver");
    ModeManager<Product> modeManager;
    Product product;

    {
        product = new Product();
    }

    public ArgumentPCommandReceiver(ModeManager<Product> modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public boolean receiveCommand(String name, char[] passwd, CommandDescription command, String[] args) throws BuildObjectException {
        product = modeManager.buildObject();
        CommandStatusResponse response = new ArgumentRequestSender<Product>().sendCommand(name, passwd, command, args, product, ServerConnectionHandler.getCurrentConnection());
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