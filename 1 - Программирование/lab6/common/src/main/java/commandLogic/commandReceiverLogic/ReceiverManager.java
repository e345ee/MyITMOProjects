package commandLogic.commandReceiverLogic;

import commandLogic.commandReceiverLogic.enums.ReceiverType;
import commandLogic.commandReceiverLogic.handlers.ReceiverHandler;
import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;

import java.util.ArrayList;
import java.util.LinkedHashMap;

//Управляет набором обработчиков (ReceiverHandler) команд.
//Поле receivers — это LinkedHashMap, сопоставляющее ReceiverType с соответствующим ReceiverHandler.
//Предоставляет методы для регистрации обработчиков и получения получателей команд по их типу (ReceiverType).
public class ReceiverManager {
    private final LinkedHashMap<ReceiverType, ReceiverHandler> receivers;

    {
        receivers = new LinkedHashMap<>();
    }

    public void registerHandler(ReceiverType type, ReceiverHandler handler) {
        receivers.put(type, handler);
    }

    public void registerReceiver(ReceiverType type, ExternalBaseReceiver receiver) {
        receivers.get(type).addReceiver(receiver);
    }

    public ArrayList<? extends ExternalBaseReceiver> getReceivers(ReceiverType type) {
        return receivers.get(type).getReceivers();
    }
}