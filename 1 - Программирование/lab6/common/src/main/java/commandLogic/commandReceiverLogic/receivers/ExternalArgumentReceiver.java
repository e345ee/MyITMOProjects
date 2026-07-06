package commandLogic.commandReceiverLogic.receivers;


//Расширяет ExternalBaseReceiver.
//Добавляет метод getArguemnt(), который возвращает аргумент типа T.
public interface ExternalArgumentReceiver<T> extends ExternalBaseReceiver {
    T getArguemnt();
}