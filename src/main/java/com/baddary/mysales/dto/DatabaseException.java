package com.baddary.mysales.dto;

public class DatabaseException extends Exception{

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    public DatabaseException(String message){
        super(message);
    }
}
