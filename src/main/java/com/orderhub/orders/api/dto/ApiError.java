package com.orderhub.orders.api.dto;

import java.sql.Timestamp;
import java.time.Instant;

public class ApiError {

    private String code;
    private String message;
    private Instant timestamp;
    private String path;

    public ApiError(String code, String message, Instant timestamp, String path) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
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
}
