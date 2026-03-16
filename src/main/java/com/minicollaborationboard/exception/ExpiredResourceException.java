package com.minicollaborationboard.exception;

public class ExpiredResourceException extends RuntimeException{

    public static final String errorCode = "RESOURCE_EXPIRED";

    public ExpiredResourceException(String message) {
        super(message);
    }
}
