package server;

import java.io.IOException;

//Этот класс является декоратором для UdpServerConnection, добавляющим возможность настраивать блокирующий или неблокирующий режим для канала DatagramChannel.
public class UdpConnectionBlockDecorator extends UdpServerConnection {
    private final UdpServerConnection baseConnection;
    private final boolean configureBlock;

    public UdpConnectionBlockDecorator(UdpServerConnection baseConnection, boolean configureBlock) throws IOException {
        super(baseConnection.channel, baseConnection.address);
        this.baseConnection = baseConnection;
        this.configureBlock = configureBlock;
        baseConnection.channel.configureBlocking(configureBlock);
    }

    public boolean getLockState() {
        return configureBlock;
    }
}