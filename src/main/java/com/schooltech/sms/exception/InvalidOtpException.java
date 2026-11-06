package com.schooltech.sms.exception;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends RuntimeException {

    private final HttpStatus status;

    public InvalidOtpException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public InvalidOtpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
