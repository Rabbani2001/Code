package com.schooltech.sms.exception;

public class HolidayNotFoundException extends RuntimeException {

    public HolidayNotFoundException(String message) {
        super(message);
    }

    public HolidayNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HolidayNotFoundException(Throwable cause) {
        super(cause);
    }
}
