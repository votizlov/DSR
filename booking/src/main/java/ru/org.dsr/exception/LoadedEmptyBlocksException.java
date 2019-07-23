package ru.org.dsr.exception;

public class LoadedEmptyBlocksException extends Exception {
    private String attr;
    private String url;
    private String by;

    public LoadedEmptyBlocksException(String attr, String url, String by) {
        this.attr = attr;
        this.url = url;
        this.by = by;
    }

    public LoadedEmptyBlocksException(String message, String attr, String url, String by) {
        super(message);
        this.attr = attr;
        this.url = url;
        this.by = by;
    }

    public String getAttr() {
        return attr;
    }

    public String getUrl() {
        return url;
    }

    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "LoadedEmptyBlocksException{" +
                "attr='" + attr + '\'' +
                ", url='" + url + '\'' +
                ", by='" + by + '\'' +
                '}';
    }
}
