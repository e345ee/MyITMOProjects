package commandLogic.commandReceiverLogic.callers;

import commandLogic.CommandDescription;
import commandLogic.commandReceiverLogic.ReceiverManager;
import commandLogic.commandReceiverLogic.enums.ReceiverType;
import commandLogic.commandReceiverLogic.receivers.ExternalArgumentReceiver;

import java.util.ArrayList;
//Работает аналогично, но для аргументов типа Organization
public class ExternalArgumentReceiverCallerO<Organization> extends ExternalCaller {

    @Override
    public void callReceivers(ReceiverManager manager, CommandDescription description, String[] lineArgs) throws Exception {
        for (ExternalArgumentReceiver<Organization> receiver : (ArrayList<ExternalArgumentReceiver<Organization>>) manager.getReceivers(ReceiverType.ArgumentRouteO)) {
            receiver.receiveCommand(description, lineArgs);
        }
    }


}