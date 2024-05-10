package com.github.gluhov.cloudfileserver.exception;

public class UserWithUsernameAlreadyExistsException extends ApiException{
    public UserWithUsernameAlreadyExistsException(String message, String errorCode) {
        super(message, errorCode);
    }
}