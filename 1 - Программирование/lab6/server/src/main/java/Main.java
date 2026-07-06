import collectionStorageManager.CustomProductSerializer;
import collectionStorageManager.FilePathExtractor;
import collectionStorageManager.JsonManager;
import collectionStorageManager.deserializers.CustomProductDeserializer;
import commandManager.ServerCommandManager;
import commandManager.commands.SaveCommand;
import logger.ServerLogger;
import models.handlers.ProductHandler;
import products.Product;
import request.RequestReader;
import request.StatusRequest;
import request.requestWorker.RequestWorkerManager;
import request.requests.ServerRequest;
import requests.AbsRequest;
import server.DatagramServerConnectionFactory;
import server.ServerConnection;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;



@SuppressWarnings("InfiniteLoopStatement")
public class Main {
    public static final int PORT = 6767;



    private static final Scanner scanner;

    static {
        scanner = new Scanner(System.in);
    }



    public static void main(String[] args) {

        System.out.println(PORT);

        try {
            ServerLogger.logger.trace("Сервер запущен на: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        }

        // setup background tasks
        var timer = new Timer(600000, event -> new SaveCommand().execute(new String[0]));
        timer.start();


        try {
            JsonManager<Product> manager = new JsonManager<>(new FilePathExtractor().getPath(args), new CustomProductDeserializer(), new CustomProductSerializer());
            ProductHandler.getInstance().setFileManager(manager);
            ProductHandler.getInstance().loadCollection();
            ServerLogger.logger.info("Загружено " + ProductHandler.getInstance().getCollection().size() + " элементов в сумме.");
        } catch (Exception e){
            ServerLogger.logger.info("Ошибка загрузки коллекции.");
            ServerLogger.logger.info(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        ServerLogger.logger.info(" ");
        ServerLogger.logger.info("Сервер ожидает запросы.");





        // connection
        ServerLogger.logger.info("Создание соединения...");
        ServerConnection connection = new DatagramServerConnectionFactory().initializeServer(PORT);
        while (true) {
            try {
                StatusRequest rq = connection.listenAndGetData();
                if (rq.getCode() < 0) {
                    ServerLogger.logger.debug("Статус код: " + rq.getCode());
                    continue;
                }

                RequestReader rqReader = new RequestReader(rq.getInputStream());
                AbsRequest baseRequest = rqReader.readObject();
                var request = new ServerRequest(baseRequest, rq.getCallerBack(), connection);
                RequestWorkerManager worker = new RequestWorkerManager();
                worker.workWithRequest(request);
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
                    ServerLogger.logger.error("Что-то пошло не так.", e1);
                }
            } catch (IOException e) {
                ServerLogger.logger.error("Что-то пошло не так", e);
            } catch (ClassNotFoundException e) {
                ServerLogger.logger.error("Класс не найден.", e);
            } catch (RuntimeException e) {
                ServerLogger.logger.fatal(e);
            }
        }
    }
}