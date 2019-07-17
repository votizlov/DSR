package ru.org.dsr.exception;

public class RobotException extends Exception {

    RobotException(String message) {
        super(message);
    }

    public RobotException() {
        super("You are robot");
    }

}
