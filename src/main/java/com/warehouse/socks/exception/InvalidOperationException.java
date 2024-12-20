package com.warehouse.socks.exception;

//Исключение при некорректной операции
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
