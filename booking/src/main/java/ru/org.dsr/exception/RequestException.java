package ru.org.dsr.exception;

public class RequestException extends Exception {
    private String url;
    private String method;
    private String[] params;

    public RequestException(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public RequestException(String message, String url, String method) {
        super(message);
        this.url = url;
        this.method = method;
    }

    public RequestException(String url, String method, String[] params) {
        this.url = url;
        this.method = method;
        this.params = params;
    }

    public RequestException(String message, String url, String method, String[] params) {
        super(message);
        this.url = url;
        this.method = method;
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public String[] getParams() {
        return params;
    }
}
