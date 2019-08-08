package ru.org.dsr.exception;

public class NoFoundElementsException extends Exception {
    private String url;
    private String select;

    public NoFoundElementsException(String url, String select) {
        this.url = url;
        this.select = select;
    }

    public NoFoundElementsException(String message, String url, String select) {
        super(message);
        this.url = url;
        this.select = select;
    }

    public String getUrl() {
        return url;
    }

    public String getSelect() {
        return select;
    }

    @Override
    public String toString() {
        return "NoFoundElementsException{" +
                "url='" + url + '\'' +
                ", select='" + select + '\'' +
                '}';
    }
}
