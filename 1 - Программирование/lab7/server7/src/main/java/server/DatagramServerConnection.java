package server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.CallerBack;
import request.StatusRequest;
import request.StatusRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;


public class DatagramServerConnection implements ServerConnection {
    private final int BUFFER_SIZE = 8192;
    private static final Logger logger = LogManager.getLogger("DatagramServerConnection");
    private final DatagramSocket ds;

    protected DatagramServerConnection(int port, int timeout) throws SocketException {
        ds = new DatagramSocket(port);
        ds.setSoTimeout(timeout);
    }

    public StatusRequest listenAndGetData() throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket dp;
        dp = new DatagramPacket(buffer, buffer.length);
        ds.receive(dp);


        logger.debug("Получено подключение.");
        logger.trace("Байты: " + Arrays.toString(dp.getData()));

        return StatusRequestBuilder.initialize().setObjectStream(new ByteArrayInputStream(dp.getData())).setCallerBack(new CallerBack(dp.getAddress(), dp.getPort())).setCode(200).build();
    }

    @Override
    public void sendData(byte[] data, InetAddress addr, int port) {
        try {
            int bufferSize = BUFFER_SIZE;  // Размер буфера
            int totalPackets = (int) Math.ceil((double) data.length / bufferSize);  // Считаем количество фрагментов

            for (int i = 0; i < totalPackets; i++) {
                int start = i * bufferSize;
                int end = Math.min(start + bufferSize, data.length);
                byte[] chunk = Arrays.copyOfRange(data, start, end);  // Берем текущую часть данных

                DatagramPacket dpToSend = new DatagramPacket(chunk, chunk.length, addr, port);
                ds.send(dpToSend);  // Отправляем фрагмент

                logger.info("Отправлен пакет " + (i + 1) + " из " + totalPackets);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // Отправляем специальный маркер, который сообщает о завершении передачи данных
            byte[] endSignal = "END".getBytes();
            DatagramPacket endPacket = new DatagramPacket(endSignal, endSignal.length, addr, port);
            ds.send(endPacket);  // Отправляем маркер окончания передачи

            logger.debug("Все данные отправлены. Отправлен сигнал завершения передачи.");
        } catch (IOException ex) {
            logger.error("Ошибка при отправке данных.", ex);
        }
    }
}
