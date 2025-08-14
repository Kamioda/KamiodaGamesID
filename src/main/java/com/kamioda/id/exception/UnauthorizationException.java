package com.kamioda.id.exception;

public class UnauthorizationException extends IllegalArgumentException {
    public UnauthorizationException(String message) {
        super(message);
    }
}
