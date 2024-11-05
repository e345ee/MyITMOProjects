package client;

import exceptions.UserNotAuthenticatedException;
import collectionStorageManager.*;

public class ClientHandler {
    private final String name;
    private final char[] passwd;
    private  long userId;

    public ClientHandler(String name, char[] passwd) {
        this.name = name;
        this.passwd = passwd;
    }

    public boolean regUser() {
        PostgresSQLManager manager = new PostgresSQLManager();
        long id = manager.regUser(name, passwd);
        if (id > 0) {
            userId = id;
            return true;
        }
        return false;
    }

    public boolean authUser() {
        PostgresSQLManager manager = new PostgresSQLManager();
        long id = manager.authUser(name, passwd);
        if (id > 0) {
            userId = id;
            return true;
        }
        return false;
    }

    public void authUserCommand() throws UserNotAuthenticatedException {
        PostgresSQLManager manager = new PostgresSQLManager();
        long id = manager.authUser(name, passwd);
        if (id > 0) {
            userId = id;
        } else {
            throw new UserNotAuthenticatedException("Невозможно выполнить команду. Юзер не авторизован.");
        }
    }

    public  long getUserId() {
        return userId;
    }
}

/**
 * 21:54:08.775 [main] ERROR PostgresSQLManager - Что-то пошло не так 0
 * org.postgresql.util.PSQLException: FATAL: remaining connection slots are reserved for roles with the SUPERUSER attribute
 *         at org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(QueryExecutorImpl.java:2553)
 *         at org.postgresql.core.v3.QueryExecutorImpl.readStartupMessages(QueryExecutorImpl.java:2665)
 *         at org.postgresql.core.v3.QueryExecutorImpl.<init>(QueryExecutorImpl.java:147)
 *         at org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:273)
 *         at org.postgresql.core.ConnectionFactory.openConnection(ConnectionFactory.java:51)
 *         at org.postgresql.jdbc.PgConnection.<init>(PgConnection.java:223)
 *         at org.postgresql.Driver.makeConnection(Driver.java:465)
 *         at org.postgresql.Driver.connect(Driver.java:264)
 *         at java.sql/java.sql.DriverManager.getConnection(DriverManager.java:681)
 *         at java.sql/java.sql.DriverManager.getConnection(DriverManager.java:190)
 *         at collectionStorageManager.PostgresSQLManager.getCollectionFromDatabase(PostgresSQLManager.java:32)
 *         at models.handlers.ProductHandler.loadCollectionFromDatabase(ProductHandler.java:45)
 *         at Main.main(Main.java:45)
 * 21:54:08.780 [main] INFO  Main - Загружено 0 элементов.
 * 21:54:08.780 [main] INFO  Main -
 */

/*
22:01:36.302 [pool-3-thread-2] INFO  DatagramServerConnection - Отправлен пакет 1 из 1
22:01:36.313 [pool-3-thread-2] INFO  ResponseSender - Ответ отправлен.
22:01:36.416 [pool-2-thread-1] ERROR PostgresSQLManager - Что-то пошло не так
22:01:36.416 [pool-2-thread-1] FATAL CommandManager - В команде предоставлено неправильное количество аргументов. Возможно, вам нужно обновить клиент
java.lang.IndexOutOfBoundsException: Index 1 out of bounds for length 1
        at jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64) ~[?:?]
        at jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70) ~[?:?]
        at jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266) ~[?:?]
        at java.util.Objects.checkIndex(Objects.java:361) ~[?:?]
        at java.util.ArrayList.get(ArrayList.java:427) ~[?:?]
        at commandManager.commands.GeneratorCommand.execute(GeneratorCommand.java:54) ~[server7-1.0-SNAPSHOT.jar:?]
        at commandManager.CommandManager.executeCommand(CommandManager.java:69) ~[server7-1.0-SNAPSHOT.jar:?]
        at request.requestWorkers.CommandClientRequestWorker.workWithRequest(CommandClientRequestWorker.java:23) ~[server7-1.0-SNAPSHOT.jar:?]
        at request.requestWorkers.RequestWorkerManager.workWithRequest(RequestWorkerManager.java:29) ~[server7-1.0-SNAPSHOT.jar:?]
        at Main.lambda$main$0(Main.java:81) ~[server7-1.0-SNAPSHOT.jar:?]
        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539) [?:?]
        at java.util.concurrent.FutureTask.run(FutureTask.java:264) [?:?]
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136) [?:?]
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635) [?:?]
        at java.lang.Thread.run(Thread.java:840) [?:?]
22:01:36.421 [pool-3-thread-2] INFO  DatagramServerConnection - Отправлен пакет 1 из 1
22:01:36.423 [pool-2-thread-4] ERROR PostgresSQLManager - Что-то пошло не так
22:01:36.423 [pool-2-thread-2] ERROR PostgresSQLManager - Что-то пошло не так
22:01:36.424 [pool-2-thread-4] ERROR CommandClientRequestWorker - Что-то пошло не так во время аутентификации: Невозможно выполнить команду. Юзер не авторизован.
22:01:36.424 [pool-2-thread-2] ERROR CommandClientRequestWorker - Что-то пошло не так во время аутентификации: Невозможно выполнить команду. Юзер не авторизован.
22:01:36.431 [pool-3-thread-2] INFO  ResponseSender - Ответ отправлен.
22:01:36.598 [pool-2-thread-3] INFO  ClearCommand - Ошибка очистки коллекции.
22:01:36.598 [pool-3-thread-2] INFO  DatagramServerConnection - Отправлен пакет 1 из 1
22:01:36.608 [pool-3-thread-2] INFO  ResponseSender - Ответ отправлен.
22:01:36.758 [pool-2-thread-1] ERROR PostgresSQLManager - Что-то пошло не так
22:01:36.759 [pool-2-thread-1] ERROR CommandClientRequestWorker - Что-то пошло не так во время аутентификации: Невозможно выполнить команду. Юзер не авторизован.
 */