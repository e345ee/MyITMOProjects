package server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        // Создаем поток для накопления полученных данных
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Проверяем, что канал открыт и подключен
        if (channel.isConnected() && channel.isOpen()) {
            // Переводим канал в неблокирующий режим для работы с Selector
            channel.configureBlocking(false);

            // Создаем селектор для отслеживания событий ввода-вывода
            Selector selector = Selector.open();
            // Регистрируем канал в селекторе для операции чтения
            channel.register(selector, SelectionKey.OP_READ);

            int i = 1; // Счетчик принятых фрагментов данных

            long timeoutMillis = 60000;  // Устанавливаем тайм-аут в 60 секунд
            long startTime = System.currentTimeMillis(); // Время последнего успешного получения данных

            while (true) {
                // Вычисляем прошедшее время с момента последнего получения данных
                long elapsedTime = System.currentTimeMillis() - startTime;
                // Вычисляем оставшееся время до истечения тайм-аута
                long remainingTime = timeoutMillis - elapsedTime;

                // Если оставшееся время меньше или равно нулю, выбрасываем исключение о превышении тайм-аута
                if (remainingTime <= 0) {
                    logger.error("Тайм-аут ожидания данных истек.");
                    selector.close(); // Закрываем селектор
                    channel.configureBlocking(true); // Возвращаем канал в блокирующий режим
                    throw new SocketTimeoutException("Превышено время ожидания данных.");
                }

                // Ожидаем события на канале в течение оставшегося времени
                int readyChannels = selector.select(remainingTime);

                // Если за указанное время не произошло никаких событий
                if (readyChannels == 0) {
                    logger.error("Тайм-аут ожидания данных истек.");
                    selector.close();
                    channel.configureBlocking(true);
                    throw new SocketTimeoutException("Превышено время ожидания данных.");
                }

                // Получаем набор ключей (каналов), готовых к операциям ввода-вывода
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove(); // Удаляем ключ из набора, чтобы избежать повторной обработки

                    // Проверяем, доступен ли канал для чтения
                    if (key.isReadable()) {
                        // Создаем буфер для приема данных
                        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
                        buf.clear();

                        // Пытаемся прочитать данные из канала
                        SocketAddress senderAddress = channel.receive(buf);

                        // Если данные были получены
                        if (senderAddress != null) {
                            buf.flip(); // Переключаем буфер в режим чтения

                            // Читаем данные из буфера
                            byte[] receivedBytes = new byte[buf.remaining()];
                            buf.get(receivedBytes);

                            // Преобразуем полученные данные в строку для проверки специального сигнала
                            String receivedString = new String(receivedBytes);
                            if (receivedString.equals("END")) {
                                // Если получен сигнал завершения передачи данных
                                logger.info("Получен сигнал завершения передачи данных.");
                                selector.close(); // Закрываем селектор
                                channel.configureBlocking(true); // Возвращаем канал в блокирующий режим
                                // Возвращаем все собранные данные
                                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                            }

                            // Записываем полученные данные в общий поток
                            byteArrayOutputStream.write(receivedBytes);
                            logger.info("Принят фрагмент данных: " + i);
                            i++;

                            // Сбрасываем таймер после успешного получения данных
                            startTime = System.currentTimeMillis();
                        }
                    }
                }
            }
        } else {
            // Если канал не подключен или закрыт, открываем соединение
            this.openConnection();
        }

        // Возвращаем все накопленные данные
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

}
