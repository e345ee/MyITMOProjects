package server;

import logger.ServerLogger;

import java.net.SocketException;

//Этот класс отвечает за создание объектов DatagramServerConnection.
public class DatagramServerConnectionFactory implements ServerConnectionFactory {

    @Override
    public ServerConnection initializeServer(int port) {
        try {
            return new DatagramServerConnection(port, 2000);
        } catch (SocketException e) {
            ServerLogger.logger.fatal("Не удается инициализировать подключение к серверу!", e);
            System.exit(-1);
        }
        return null;
    }

    @Override
    public ServerConnection initializeServer(int port, int timeout) {
        try {
            return new DatagramServerConnection(port, timeout);
        } catch (SocketException e) {
            ServerLogger.logger.fatal("Невозможно проинициализировать соединение с сервером!", e);
            System.exit(-1);
        }
        return null;
    }
}