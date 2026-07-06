package commandLogic.commandReceiverLogic.callers;

import commandLogic.CommandDescription;
import commandLogic.commandReceiverLogic.ReceiverManager;
import commandLogic.commandReceiverLogic.enums.ReceiverType;
import commandLogic.commandReceiverLogic.receivers.ExternalArgumentReceiver;

import java.util.ArrayList;

public class ExternalArgumentReceiverCallerO<Organization> extends ExternalCaller {
    @Override
    public void callReceivers(String name, char[] passwd, ReceiverManager manager, CommandDescription description, String[] lineArgs) throws Exception {
        for (ExternalArgumentReceiver<Organization> receiver : (ArrayList<ExternalArgumentReceiver<Organization>>) manager.getReceivers(ReceiverType.ArgumentRouteO)) {
            receiver.receiveCommand(name, passwd, description, lineArgs);
        }
    }
}
