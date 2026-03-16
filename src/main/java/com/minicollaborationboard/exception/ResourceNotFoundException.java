package com.minicollaborationboard.exception;

public class ResourceNotFoundException extends RuntimeException {

    public static final String errorCode = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
