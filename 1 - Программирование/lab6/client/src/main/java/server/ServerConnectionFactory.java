package server;

import java.io.IOException;
import java.net.InetAddress;
//ServerConnectionFactory:
//Описывает метод openConnection, который открывает новое соединение с сервером на заданный хост и порт.
public interface ServerConnectionFactory {
    ServerConnection openConnection(InetAddress host, int port) throws IOException;
}
