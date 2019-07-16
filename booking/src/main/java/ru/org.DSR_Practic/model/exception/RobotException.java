package ru.org.DSR_Practic.model.exception;

public class RobotException extends Exception {
    private final static String msg = "You are robot";

    RobotException(String msg) {
        super(msg);
    }

    public RobotException() {
        this(msg);
    }

}
