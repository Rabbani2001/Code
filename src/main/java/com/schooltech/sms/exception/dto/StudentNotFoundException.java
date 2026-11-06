package com.schooltech.sms.exception.dto;

import org.springframework.http.HttpStatus;

public class StudentNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public StudentNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public StudentNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
