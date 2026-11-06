package com.schooltech.sms.exception;

public class SupervisorNotFoundException extends RuntimeException {

    public SupervisorNotFoundException(String message) {
        super(message);
    }

    public SupervisorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SupervisorNotFoundException(Throwable cause) {
        super(cause);
    }
}
