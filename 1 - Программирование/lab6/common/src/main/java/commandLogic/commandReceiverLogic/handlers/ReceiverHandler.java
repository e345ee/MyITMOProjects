package commandLogic.commandReceiverLogic.handlers;

import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;

import java.util.ArrayList;

//ReceiverHandler: абстрактный класс, представляющий обработчик получателей команд. У него есть два метода:
public abstract class ReceiverHandler {
    public abstract void addReceiver(ExternalBaseReceiver receiver);

    public abstract ArrayList<? extends ExternalBaseReceiver> getReceivers();
}
