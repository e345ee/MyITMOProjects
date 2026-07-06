
import commandManager.ServerCommandManager;
import commandManager.commands.SaveCommand;
import models.handlers.ProductHandler;
import multithreading.MultithreadingManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.RequestReader;
import request.StatusRequest;
import request.requestWorkers.RequestWorkerManager;
import request.requests.ServerRequest;
import requests.AbsRequest;
import server.DatagramServerConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;

import server.ServerConnection;

import javax.swing.*;

@SuppressWarnings("InfiniteLoopStatement")
public class Main {
    public static final int PORT = 6767;
    private static final Logger logger = LogManager.getLogger("Main");


    public static void main(String[] args) {

        ProductHandler loader = ProductHandler.getInstance();


        logger.trace("Сервер запущен!");


        // var timer = new Timer(600000, event -> new SaveCommand().execute(new String[0]));
        //timer.start();

        // load collection
        try {

            loader.loadCollectionFromDatabase();
            if (loader.getCollection() == null) {
                logger.info("Коллекция пуста.");
            } else {
                logger.info("Загружено " + loader.getCollection().size() + " элементов.");
            }
            logger.info(" ");

            logger.info("Сейчас сервер прослушивает запросы.");
        } catch (Exception e) {
            logger.info("Ошибка загрузки коллекции.");
            logger.info(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        ExecutorService requestProcessingThreadPool = MultithreadingManager.getRequestThreadPool();

        // connection
        logger.info("Создание соединения");
        ServerConnection connection = new DatagramServerConnectionFactory().initializeServer(PORT);
        while (true) {
            try {
                StatusRequest rq = connection.listenAndGetData();
                if (rq.getCode() < 0) {
                    logger.debug("Статус: " + rq.getCode());
                    continue;
                }
                new Thread(() -> {
                    try {

                        RequestReader rqReader = new RequestReader(rq.getInputStream());
                        AbsRequest baseRequest = rqReader.readObject();
                        var request = new ServerRequest(baseRequest, rq.getCallerBack(), connection);
                        RequestWorkerManager worker = new RequestWorkerManager();
                        // Process requests using the fixed thread pool
                        requestProcessingThreadPool.submit(() -> worker.workWithRequest(request));
                    } catch (IOException | ClassNotFoundException e) {
                        logger.error("Ошибка создания потока", e);
                    }
                }).start();
            } catch (SocketTimeoutException e) {
                // Check if there's any input available in System.in
                try {
                    if (System.in.available() > 0) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        String line = reader.readLine();
                        if (line != null) {
                            ServerCommandManager manager = new ServerCommandManager();
                            manager.executeCommand(line.split("\\s+"));
                        }
                    }
                } catch (IOException e1) {
                    logger.error("Что-то пошло не так.", e1);
                }
            } catch (IOException e) {
                logger.error("Что-то пошло не так.", e);
            } catch (RuntimeException e) {
                logger.fatal(e);
            }
        }
    }
}