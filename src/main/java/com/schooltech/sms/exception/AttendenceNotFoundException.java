package com.schooltech.sms.exception;

public class AttendenceNotFoundException extends RuntimeException {

    public AttendenceNotFoundException(String message) {
        super(message);
    }

    public AttendenceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttendenceNotFoundException(Throwable cause) {
        super(cause);
    }
}
