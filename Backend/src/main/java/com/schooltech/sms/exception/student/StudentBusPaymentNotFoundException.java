package com.schooltech.sms.exception.student;

public class StudentBusPaymentNotFoundException extends RuntimeException {

    public StudentBusPaymentNotFoundException(String message) {
        super(message);
    }

    public StudentBusPaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StudentBusPaymentNotFoundException(Throwable cause) {
        super(cause);
    }
}
