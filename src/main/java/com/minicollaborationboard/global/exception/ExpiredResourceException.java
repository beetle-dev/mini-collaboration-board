package com.minicollaborationboard.global.exception;

public class ExpiredResourceException extends RuntimeException{

    static final String errorCode = "RESOURCE_EXPIRED";

    public ExpiredResourceException(String message) {
        super(message);
    }
}
