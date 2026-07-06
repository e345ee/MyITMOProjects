package commandLogic.commandReceiverLogic.handlers;

import commandLogic.commandReceiverLogic.receivers.ExternalArgumentReceiver;
import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;

import java.util.ArrayList;

public class ArgumentReceiverHandlerO<Organization> extends ReceiverHandler {

    private final ArrayList<ExternalArgumentReceiver<Organization>> receivers;
    Class<Organization> argType;

    {
        receivers = new ArrayList<>();
    }

    public ArgumentReceiverHandlerO(Class<Organization> argType) {
        this.argType = argType;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void addReceiver(ExternalBaseReceiver receiver) {
        if (receiver instanceof ExternalArgumentReceiver<?>) {
            if (((ExternalArgumentReceiver<?>) receiver).getArguemnt().getClass().getName().equals(argType.getName()))
                receivers.add((ExternalArgumentReceiver<Organization>) receiver);
        }
    }

    @Override
    public ArrayList<ExternalArgumentReceiver<Organization>> getReceivers() {
        return receivers;
    }
}
