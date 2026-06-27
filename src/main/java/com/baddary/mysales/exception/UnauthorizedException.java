package com.baddary.mysales.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}