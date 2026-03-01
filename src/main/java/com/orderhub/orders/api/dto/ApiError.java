package com.orderhub.orders.api.dto;

import java.time.Instant;
import java.util.List;

public class ApiError {

    private String code;
    private String message;
    private Instant timestamp;
    private String path;
    private List<FieldError> errors;

    public ApiError(String code, String message, Instant timestamp, String path) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }

    public ApiError(String code, String message, Instant timestamp, String path, List<FieldError> erros) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
        this.errors = erros;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public List<FieldError> getErrors() {
        return errors;
    }
}
