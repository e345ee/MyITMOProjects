import client.Client;
import commandManager.CommandDescriptionHolder;
import commandManager.CommandExecutor;
import commandManager.CommandLoaderUtility;
import commandManager.CommandMode;
import exceptions.CommandsNotLoadedException;
import request.AuthRequestSender;
import request.RegRequestSender;
import responses.AuthResponse;
import responses.RegResponse;
import server.*;

import javax.swing.*;
import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validators.passwords.NameVal;

public class Main {
    public static final int PORT = 6767;
    private static final Logger logger = LogManager.getLogger("Main");

    /**
     * Program entry point.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // server connecting
        try {
            //ServerConnection connection =
            ServerConnection connection = new UdpConnectionBlockDecorator((UdpServerConnection) new UdpServerConnectionFactory().openConnection(InetAddress.getByName("192.168.10.80"), PORT), true);
            ServerConnectionHandler.setServerConnection(connection);
            connection.openConnection();

            System.out.println("\nАвторизуйтесь или Зарегистрируйтесь:\n");
            String name;
            char[] passwd;
            String nextLine;
            String auth;
            boolean authorizationDone = false;


            do {
                System.out.println("Введите:\n  1 - для авторизации,\n  2 - для регистрации\n");
                try {
                    Console console = System.console();
                    if (console == null) {
                        System.out.println("Консоль недоступна.");
                        System.exit(1);
                    }


                    while (true) {
                        // Читаем строку из консоли
                        String input = console.readLine().trim();

                        // Проверяем, является ли ввод '1' или '2'
                        if ("1".equals(input) || "2".equals(input)) {
                            System.out.println("Вы ввели: " + input);
                            auth = input;
                            break; // Выходим из цикла, если условие выполнено
                        } else {
                            System.out.println("Некорректный ввод. Попробуйте снова.");
                        }
                    }



                    while (true) {
                        System.out.print("Введите ваше имя: ");
                        nextLine = console.readLine().trim();
                        try {
                            name = new NameVal().validate(nextLine);
                            break;
                        } catch (IllegalArgumentException e){
                            System.out.println("Некорректный ввод. Попробуйте снова.");
                        }


                    }

                    while (true) {
                        System.out.print("Введите пароль: ");
                        passwd = console.readPassword();

                        if (passwd != null && passwd.length > 0) {
                            break;
                        } else System.out.println("Некорректный ввод попробуйте снова.");
                    }

                    if ("1".equals(auth)) {
                        System.out.println("Проверка на существование в базе данных.");
                        AuthRequestSender rqSender = new AuthRequestSender();
                        AuthResponse response = rqSender.sendAuthData(name, passwd, ServerConnectionHandler.getCurrentConnection());
                        if (response.isAuth()) {
                            Client.getInstance(name, passwd);
                            System.out.println("Успех!");
                            authorizationDone = true;
                        } else
                            System.out.println("Такой юзер не обнаружен");
                    } else {
                        System.out.println("Регистрация...");
                        RegRequestSender rqSender = new RegRequestSender();
                        RegResponse response = rqSender.sendRegData(name, passwd, ServerConnectionHandler.getCurrentConnection());
                        if (response.isReg()) {
                            System.out.println("Успешно зарегистрирован:\n имя: " + name + ",\n пароль: " + Arrays.toString(passwd));
                        } else
                            System.out.println("Юзер с таким именем уже существует.");
                    }
                } catch (Exception e) {
                    System.out.println("При вводе произошла непредвиденная ошибка!" + e.getMessage());
                }
            } while (!authorizationDone);

            // request commands
            boolean commandsNotLoaded = true;
            int waitingCount = 4000;
            while (commandsNotLoaded) {
                try {
                    CommandLoaderUtility.initializeCommands();
                    CommandExecutor executor = new CommandExecutor(CommandDescriptionHolder.getInstance().getCommands(), System.in, CommandMode.UserMode);
                    commandsNotLoaded = false;

                    // start executing
                    System.out.println("Добро пожаловать в приложение.");
                    executor.startExecuting();
                } catch (CommandsNotLoadedException e) {
                    logger.warn("Не удалось получить команды с сервера.");

                    AtomicInteger secondsRemained = new AtomicInteger(waitingCount / 1000 - 1);

                    javax.swing.Timer timer = new Timer(1000, (x) -> logger.info("Повторная отправка запроса через " + secondsRemained.getAndDecrement() + " секунд."));

                    timer.start();

                    CountDownLatch latch = new CountDownLatch(1);


                    int finalWaitingCount = waitingCount;
                    Runnable wait = () -> {
                        try {
                            Thread.sleep(finalWaitingCount);
                            latch.countDown();
                        } catch (InterruptedException ex) {
                            logger.info("Продолжение...");
                        }
                    };

                    Runnable forceInterrupt = () -> {
                        try {
                            int ignored = System.in.read();
                            latch.countDown();
                        } catch (IOException ex) {
                            logger.error("Что-то пошло не так.");
                        }
                    };


                    Thread tWait = new Thread(wait);
                    Thread tForceInt = new Thread(forceInterrupt);

                    tWait.start();
                    tForceInt.start();

                    try {
                        latch.await();
                        timer.stop();

                        tWait.interrupt();
                    } catch (InterruptedException ex) {
                        logger.info("Прервано");
                    }

                    waitingCount += 2000;
                }
            }
        } catch (UnknownHostException ex) {
            logger.fatal("Невозможно найти хост.");
        } catch (IOException ex) {
            logger.error("Что-то пошло не так.");
        }
    }
}