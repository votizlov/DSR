package ru.org.dsr.exception;

public class JSONImproperHandling extends Exception {
    private String JSONResponse;
    private String description;

    public JSONImproperHandling(String message, String JSONResponse, String description) {
        super(message);
        this.JSONResponse = JSONResponse;
        this.description = description;
    }

    public JSONImproperHandling(String JSONResponse, String description) {
        super();
        this.JSONResponse = JSONResponse;
        this.description = description;
    }

    public String getJSONResponse() {
        return JSONResponse;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "JSONImproperHandling{" +
                "JSONResponse='" + JSONResponse + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
