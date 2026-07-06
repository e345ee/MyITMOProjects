package server;

import logger.ServerLogger;
import request.CallerBack;
import request.StatusRequest;
import request.StatusRequestBuilder;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;


//Этот класс реализует работу с серверным соединением через UDP-протокол с использованием DatagramSocket
public class DatagramServerConnection implements ServerConnection {
    private final int BUFFER_SIZE = 8192;
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


        ServerLogger.logger.debug("Получено подключение.");
        ServerLogger.logger.trace("Байты: " + Arrays.toString(dp.getData()));

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

                ServerLogger.logger.info("Отправлен пакет " + (i + 1) + " из " + totalPackets);
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

            ServerLogger.logger.debug("Все данные отправлены. Отправлен сигнал завершения передачи.");
        } catch (IOException ex) {
            ServerLogger.logger.error("Ошибка при отправке данных.", ex);
        }
    }
}
