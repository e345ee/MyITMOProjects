package commandLogic.commandReceiverLogic.handlers;

import commandLogic.commandReceiverLogic.receivers.ExternalArgumentReceiver;
import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;

import java.util.ArrayList;

//Управляет получателями команд с аргументом типа Product.
//Метод addReceiver добавляет получателя, если он является ExternalArgumentReceiver и его аргумент соответствует типу Product.
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
