package commandManager.externalRecievers;

import commandLogic.CommandDescription;
import commandLogic.commandReceiverLogic.receivers.ExternalArgumentReceiver;
import exceptions.BuildObjectException;
import mods.ModeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import products.Organization;
import products.Product;
import request.ArgumentRequestSender;
import responses.CommandStatusResponse;
import server.ServerConnectionHandler;

public class ArgumentOrgCommandReceiver implements ExternalArgumentReceiver<Organization> {

    private static final Logger logger = LogManager.getLogger("ArgumentOrgCommandReceiver");
    ModeManager<Organization> modeManager;
    Organization organization;

    {
        organization = new Organization();
    }

    public ArgumentOrgCommandReceiver(ModeManager<Organization> modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public boolean receiveCommand(String name, char[] passwd, CommandDescription command, String[] args) throws BuildObjectException {
        organization = modeManager.buildObject();
        CommandStatusResponse response = new ArgumentRequestSender<Organization>().sendCommand(name, passwd, command, args, organization, ServerConnectionHandler.getCurrentConnection());
        if (response != null) {
            logger.info("Status code: " + response.getStatusCode());
            logger.info("Response: \n" + response.getResponse());
            return true;
        }
        return false;
    }

    @Override
    public Organization getArguemnt() {
        return organization;
    }
}
