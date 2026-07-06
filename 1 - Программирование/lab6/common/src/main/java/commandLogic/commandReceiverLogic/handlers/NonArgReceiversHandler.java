package commandLogic.commandReceiverLogic.handlers;

import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;

import java.util.ArrayList;

//Управляет получателями команд без аргументов.
public class NonArgReceiversHandler extends ReceiverHandler {

    private final ArrayList<ExternalBaseReceiver> receivers;

    {
        receivers = new ArrayList<>();
    }

    @Override
    public void addReceiver(ExternalBaseReceiver receiver) {
        receivers.add(receiver);
    }

    @Override
    public ArrayList<ExternalBaseReceiver> getReceivers() {
        return receivers;
    }
}