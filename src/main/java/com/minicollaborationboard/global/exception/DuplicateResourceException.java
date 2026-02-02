package com.minicollaborationboard.global.exception;

public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String message) {
        super(message);
        String errorCode = "DUPLICATE_RESOURCE";
    }
}
