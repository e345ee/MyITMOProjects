package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

//Реализует интерфейс ServerConnection, который предоставляет методы для открытия, закрытия соединения и отправки данных.
public class UdpServerConnection implements ServerConnection {

    public static final int BUFFER_SIZE = 8192;

    private static final Logger logger = LogManager.getLogger("UdpServerConnection");
    private final ExecutorService service;
    protected final DatagramChannel channel;
    protected SocketAddress address;

    {
        service = Executors.newCachedThreadPool();
    }

    protected UdpServerConnection(DatagramChannel channel, SocketAddress address) {
        this.channel = channel;
        this.address = address;
    }


    /**
     * Method for open a connection.
     */
    @Override
    public void openConnection() throws IOException {
        if (!channel.isConnected()) {
            channel.connect(address);
            logger.log(Level.INFO, "Открыт канал на: " + address);
        }
    }

    /**
     * Method for close a connection
     */
    @Override
    public void closeConnection() throws IOException {
        if (channel.isConnected() && channel.isOpen()) {
            try {
                channel.disconnect();
                channel.close();
            } catch (IOException e) {
                channel.close();
            }
        }
    }

    Future<ByteArrayInputStream> bosFuture;
    boolean lastRequestSuccess = true;

    @Override
    public ByteArrayInputStream sendData(byte[] bytesToSend) throws IOException {
        ByteArrayInputStream bos = null;
        if (channel.isConnected() && channel.isOpen()) {
            var buf = ByteBuffer.wrap(bytesToSend);
            channel.send(buf, address);

            if (lastRequestSuccess) {
                Callable<ByteArrayInputStream> callable = this::listenServer;
                bosFuture = service.submit(callable);
            }

            try {
                bos = bosFuture.get(6000, TimeUnit.SECONDS);
                lastRequestSuccess = true;
            } catch (InterruptedException e) {
                logger.info("Прервано.");

            } catch (ExecutionException e) {
                lastRequestSuccess = true;
                logger.error("Что-то пошло не так.");

                throw (IOException) e.getCause();
            } catch (TimeoutException e) {
                lastRequestSuccess = false;
                logger.error("Время ожидания запроса вышло.");
                throw new PortUnreachableException("Сервер может быть недоступен.");
            }
        } else this.openConnection();
        return bos;
    }

    private ByteArrayInputStream listenServer() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (channel.isConnected() && channel.isOpen()) {
            // Переводим канал в неблокирующий режим
            channel.configureBlocking(false);

            Selector selector = Selector.open();
            // Регистрируем канал в селекторе для операции чтения
            channel.register(selector, SelectionKey.OP_READ);

            int i = 1;

            long timeoutMillis = 60000;  // 60 секунд
            long startTime = System.currentTimeMillis();

            while (true) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long remainingTime = timeoutMillis - elapsedTime;

                if (remainingTime <= 0) {
                    logger.error("Тайм-аут ожидания данных истек.");
                    selector.close();
                    channel.configureBlocking(true); // Возвращаем канал в блокирующий режим
                    throw new SocketTimeoutException("Превышено время ожидания данных.");
                }

                // Ожидаем события с оставшимся временем
                int readyChannels = selector.select(remainingTime);

                if (readyChannels == 0) {
                    // Тайм-аут истек без получения данных
                    logger.error("Тайм-аут ожидания данных истек.");
                    selector.close();
                    channel.configureBlocking(true);
                    throw new SocketTimeoutException("Превышено время ожидания данных.");
                }

                // регистратор событий для канала.
// Он содержит информацию о зарегистрированных событиях для конкретного канала и предоставляет методы для их обработки
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isReadable()) {
                        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
                        buf.clear();

                        // Читаем данные из канала
                        SocketAddress senderAddress = channel.receive(buf);

                        if (senderAddress != null) {
                            buf.flip();

                            byte[] receivedBytes = new byte[buf.remaining()];
                            buf.get(receivedBytes);

                            // Проверяем сигнал окончания передачи
                            String receivedString = new String(receivedBytes);
                            if (receivedString.equals("END")) {
                                logger.info("Получен сигнал завершения передачи данных.");
                                selector.close();
                                channel.configureBlocking(true);
                                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                            }

                            byteArrayOutputStream.write(receivedBytes);  // Записываем данные
                            logger.info("Принят фрагмент данных: " + i);
                            i++;

                            // Сбрасываем таймер после успешного получения данных
                            startTime = System.currentTimeMillis();
                        }
                    }
                }
            }
        } else {
            this.openConnection();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


}
