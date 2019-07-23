package ru.org.dsr.model;

import ru.Application;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//Singleton
public class MainLog {
    private static Logger log;

    static {
        try {
            LogManager.getLogManager().readConfiguration(
                    Application.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        log = Logger.getLogger(MainLog.class.getName());
    }

    private MainLog() {}

    public static Logger getLog() {
        return log;
    }
}
