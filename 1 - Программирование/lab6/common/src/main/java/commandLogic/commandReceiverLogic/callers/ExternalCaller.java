package commandLogic.commandReceiverLogic.callers;

import commandLogic.CommandDescription;
import commandLogic.commandReceiverLogic.ReceiverManager;

import java.io.Serializable;

//Абстрактный класс, который отвечает за вызов получателей команд.
public abstract class ExternalCaller implements Serializable {
    public abstract void callReceivers(ReceiverManager manager, CommandDescription description, String[] lineArgs) throws Exception;
}