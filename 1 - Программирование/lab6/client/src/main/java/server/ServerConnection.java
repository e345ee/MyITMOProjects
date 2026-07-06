package server;

import java.io.IOException;
import java.io.InputStream;

//ServerConnection:
//Описывает методы для открытия, закрытия соединения и отправки данны
public interface ServerConnection {

    void openConnection() throws IOException;


    void closeConnection() throws IOException;


    InputStream sendData(byte[] bytesToSend) throws IOException;
}
