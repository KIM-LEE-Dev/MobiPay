package com.example.merchant.domain.parking.error;

public class DuplicatedParkingException extends IllegalArgumentException {

    public DuplicatedParkingException() {
        super();
    }

    public DuplicatedParkingException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
