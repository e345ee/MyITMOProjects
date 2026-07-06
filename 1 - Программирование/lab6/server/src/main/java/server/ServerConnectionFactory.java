package server;

public interface ServerConnectionFactory {
    ServerConnection initializeServer(int port);

    ServerConnection initializeServer(int port, int timeout);
}