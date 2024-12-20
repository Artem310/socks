package com.warehouse.socks.exception;

//Исключение при недостаточном количестве носков
public class InsufficientSocksException extends RuntimeException {
    public InsufficientSocksException(String message) {
        super(message);
    }
}
