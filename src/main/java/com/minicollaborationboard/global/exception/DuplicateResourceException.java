package com.minicollaborationboard.global.exception;

public class DuplicateResourceException extends RuntimeException{

    public final static String errorCode = "DUPLICATE_RESOURCE";

    public DuplicateResourceException(String message) {
        super(message);
    }
}
