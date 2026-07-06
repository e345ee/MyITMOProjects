package server;

//Этот класс служит для управления текущим соединением с сервером:
//
//ServerConnectionHandler:
//Хранит текущее активное соединение и предоставляет методы для его установки и получения.
//Методы setServerConnection и getCurrentConnection позволяют клиентскому коду обращаться к текущему соединению.
public class ServerConnectionHandler {

    private static ServerConnection currentConnection;

    public static void setServerConnection(ServerConnection connection) {
        currentConnection = connection;
    }

    public static ServerConnection getCurrentConnection() {
        return currentConnection;
    }
}
