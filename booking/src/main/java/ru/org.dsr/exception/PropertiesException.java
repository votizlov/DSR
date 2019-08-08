package ru.org.dsr.exception;

public class PropertiesException extends Exception {
    private String message;

    public PropertiesException(String message) {
        this.message = message;
    }

    public PropertiesException(String message, String need) {
        super(message);
        this.message = need;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PropertiesException{" +
                "message='" + message + '\'' +
                '}';
    }
}
