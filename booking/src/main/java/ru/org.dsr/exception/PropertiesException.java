package ru.org.dsr.exception;

public class PropertiesException extends Exception {
    private String need;

    public PropertiesException(String need) {
        this.need = need;
    }

    public PropertiesException(String message, String need) {
        super(message);
        this.need = need;
    }

    public String getNeed() {
        return need;
    }
}
