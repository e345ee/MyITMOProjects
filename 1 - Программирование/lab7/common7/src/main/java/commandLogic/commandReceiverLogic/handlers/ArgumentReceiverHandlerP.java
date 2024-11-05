package commandLogic.commandReceiverLogic.handlers;

import commandLogic.commandReceiverLogic.receivers.ExternalArgumentReceiver;
import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;

import java.util.ArrayList;

public class ArgumentReceiverHandlerP<Product> extends ReceiverHandler {

    private final ArrayList<ExternalArgumentReceiver<Product>> receivers;
    Class<Product> argType;

    {
        receivers = new ArrayList<>();
    }

    public ArgumentReceiverHandlerP(Class<Product> argType) {
        this.argType = argType;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void addReceiver(ExternalBaseReceiver receiver) {
        if (receiver instanceof ExternalArgumentReceiver<?>) {
            if (((ExternalArgumentReceiver<?>) receiver).getArguemnt().getClass().getName().equals(argType.getName()))
                receivers.add((ExternalArgumentReceiver<Product>) receiver);
        }
    }

    @Override
    public ArrayList<ExternalArgumentReceiver<Product>> getReceivers() {
        return receivers;
    }
}

