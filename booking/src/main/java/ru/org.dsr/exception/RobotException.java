package ru.org.dsr.exception;

public class RobotException extends Exception {

    private String srcForRobot;

    public RobotException() {}

    public RobotException(String srcForRobot) {
        this.srcForRobot = srcForRobot;
    }

    public RobotException(String message, String srcForRobot) {
        super(message);
        this.srcForRobot = srcForRobot;
    }

    public String getSrcForRobot() {
        return srcForRobot;
    }
}
