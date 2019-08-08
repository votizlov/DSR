package ru.org.dsr.exception;

public class LoadedEmptyBlocksException extends Exception {
    private String select;
    private String url;
    private String attr;

    public LoadedEmptyBlocksException(String select, String url, String attr) {
        this.select = select;
        this.url = url;
        this.attr = attr;
    }

    public LoadedEmptyBlocksException(String message, String select, String url, String attr) {
        super(message);
        this.select = select;
        this.url = url;
        this.attr = attr;
    }

    public String getSelect() {
        return select;
    }

    public String getUrl() {
        return url;
    }

    public String getAttr() {
        return attr;
    }

    @Override
    public String toString() {
        return "LoadedEmptyBlocksException{" +
                "select='" + select + '\'' +
                ", url='" + url + '\'' +
                ", attr='" + attr + '\'' +
                '}';
    }
}
