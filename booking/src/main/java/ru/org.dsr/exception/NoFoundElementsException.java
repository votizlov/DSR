package ru.org.dsr.exception;

public class NoFoundElementsException extends Exception {
    private String from;
    private String by;

    public NoFoundElementsException(String from, String by) {
        this.from = from;
        this.by = by;
    }

    public NoFoundElementsException(String message, String from, String by) {
        super(message);
        this.from = from;
        this.by = by;
    }

    public String getFrom() {
        return from;
    }

    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "NoFoundElementsException{" +
                "from='" + from + '\'' +
                ", by='" + by + '\'' +
                '}';
    }
}
