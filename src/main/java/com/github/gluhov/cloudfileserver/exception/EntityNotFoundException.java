package com.github.gluhov.cloudfileserver.exception;

public class EntityNotFoundException extends ApiException {

    public EntityNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}