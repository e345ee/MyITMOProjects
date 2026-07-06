import commandManager.CommandDescriptionHolder;
import commandManager.CommandExecutor;
import commandManager.CommandLoaderUtility;
import commandManager.CommandMode;
import exceptions.CommandsNotLoadedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.*;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static final int PORT = 6767;
    private static final Logger logger = LogManager.getLogger("main");


    public static void main(String[] args) {

        try {

                //"192.168.10.80"
            ServerConnection connection = new UdpConnectionBlockDecorator((UdpServerConnection) new UdpServerConnectionFactory().openConnection(InetAddress.getByName("192.168.10.80"), PORT), false);
            ServerConnectionHandler.setServerConnection(connection);
            connection.openConnection();

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
