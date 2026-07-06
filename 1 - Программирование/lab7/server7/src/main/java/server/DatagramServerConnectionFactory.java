package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketException;

public class DatagramServerConnectionFactory implements ServerConnectionFactory {
    private static final Logger logger = LogManager.getLogger("DatagramServerConnectionFactory");

    @Override
    public ServerConnection initializeServer(int port) {
        try {
            return new DatagramServerConnection(port, 2000);
        } catch (SocketException e) {
            logger.fatal("Невозможно инициализировать соединение с сервером!", e);
            System.exit(-1);
        }
        return null;
    }

    @Override
    public ServerConnection initializeServer(int port, int timeout) {
        try {
            return new DatagramServerConnection(port, timeout);
        } catch (SocketException e) {
            logger.fatal("Невозможно инициализировать соединение с сервером!", e);
            System.exit(-1);
        }
        return null;
    }
}
