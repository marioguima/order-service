package com.orderhub.orders.domain.exception;

public class BadRquestException extends RuntimeException {

    public BadRquestException(String message) {
        super(message);
    }
}
