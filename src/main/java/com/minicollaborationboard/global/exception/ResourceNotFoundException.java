package com.minicollaborationboard.global.exception;

public class ResourceNotFoundException extends RuntimeException {

    final static String errorCode = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
