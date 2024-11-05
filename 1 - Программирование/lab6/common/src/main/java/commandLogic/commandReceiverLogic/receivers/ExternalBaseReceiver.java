package commandLogic.commandReceiverLogic.receivers;

import commandLogic.CommandDescription;

//Базовый интерфейс для всех классов, которые могут обрабатывать команды.
//Метод receiveCommand(CommandDescription command, String[] args): метод, который выполняет
// команду и принимает описание команды и аргументы в виде массива строк.
public interface ExternalBaseReceiver {
    boolean receiveCommand(CommandDescription command, String[] args) throws Exception;
}