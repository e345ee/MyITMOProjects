package commandLogic.commandReceiverLogic.callers;

import commandLogic.CommandDescription;
import commandLogic.commandReceiverLogic.ReceiverManager;
import commandLogic.commandReceiverLogic.enums.ReceiverType;
import commandLogic.commandReceiverLogic.receivers.ExternalArgumentReceiver;

import java.util.ArrayList;

//Вызывает обработку команд для получателей с аргументом типа Product.
public class ExternalArgumentReceiverCallerP<Product> extends ExternalCaller {

    @Override
    public void callReceivers(ReceiverManager manager, CommandDescription description, String[] lineArgs) throws Exception {
        for (ExternalArgumentReceiver<Product> receiver : (ArrayList<ExternalArgumentReceiver<Product>>) manager.getReceivers(ReceiverType.ArgumentRouteP)) {
            receiver.receiveCommand(description, lineArgs);
        }
    }


}