package server;

import java.io.IOException;
import java.io.InputStream;

public interface ServerConnection {

    void openConnection() throws IOException;


    void closeConnection() throws IOException;


    InputStream sendData(byte[] bytesToSend) throws IOException;
}
