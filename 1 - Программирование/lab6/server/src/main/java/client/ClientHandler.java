package client;

import exceptions.NotAvailableException;
import logger.ServerLogger;
import request.CallerBack;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Этот класс отвечает за управление клиентами и их соединениями
public class ClientHandler implements ActionListener {
    private static ClientHandler instance;
    private final javax.swing.Timer timer;
    boolean availability = true;
    private CallerBack callerBack;

    private ClientHandler() {
        timer = new Timer(300000, this);
    }

    public static ClientHandler getInstance() {
        if (instance == null)
            instance = new ClientHandler();
        return instance;
    }

    public void approveCallerBack(CallerBack callerBack) throws NotAvailableException {
        if (availability || this.callerBack.equals(callerBack)) {
            this.callerBack = callerBack;
            availability = true;
            timer.start();
        } else throw new NotAvailableException(callerBack);
    }

    public boolean isAvailability() {
        return availability;
    }

    public void allowNewCallerBack() {
        availability = true;
    }

    public void restartTimer() {
        ServerLogger.logger.info("Таймер перезапущен!");
        this.timer.restart();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ServerLogger.logger.debug("Разрешено новое подключение.");
        allowNewCallerBack();
    }
}
