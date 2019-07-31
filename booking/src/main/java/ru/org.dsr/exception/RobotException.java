package ru.org.dsr.exception;


import org.jsoup.nodes.Document;

public class RobotException extends Exception {

    private Document srcForRobot;

    public RobotException(Document srcForRobot) {
        this.srcForRobot = srcForRobot;
    }

    public RobotException(String message, Document srcForRobot) {
        super(message);
        this.srcForRobot = srcForRobot;
    }

    public RobotException(String message) {
        super(message);
    }

    public Document getSrcForRobot() {
        return srcForRobot;
    }

    @Override
    public String toString() {
        if (srcForRobot != null) {
            String page = srcForRobot.toString();
            page = page.length() > 8192 ? page.substring(0, 8192) : page;
            return "RobotException{" +
                    "srcForRobot='" + page + '\'' +
                    '}';
        } else return super.toString();
    }
}
