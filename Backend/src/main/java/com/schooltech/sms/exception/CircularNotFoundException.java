package com.schooltech.sms.exception;

public class CircularNotFoundException extends RuntimeException {

    public CircularNotFoundException(String message) {
        super(message);
    }

    public CircularNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CircularNotFoundException(Throwable cause) {
        super(cause);
    }
}
