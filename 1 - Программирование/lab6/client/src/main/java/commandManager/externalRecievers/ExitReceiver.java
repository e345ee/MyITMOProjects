package commandManager.externalRecievers;

import commandLogic.CommandDescription;
import commandLogic.commandReceiverLogic.receivers.ExternalBaseReceiver;

import java.util.Objects;

//Этот обработчик отвечает за команду exit. Когда вызывается команда exit, приложение завершает свою работу.
public class ExitReceiver implements ExternalBaseReceiver {

    @Override
    public boolean receiveCommand(CommandDescription command, String[] args) {
        if (!Objects.equals(command.getName(), "exit")) return true;

        System.exit(0);
        return false;
    }
}