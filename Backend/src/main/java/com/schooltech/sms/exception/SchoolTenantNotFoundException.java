package com.schooltech.sms.exception;

public class SchoolTenantNotFoundException extends RuntimeException {

    public SchoolTenantNotFoundException(String message) {
        super(message);
    }

    public SchoolTenantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchoolTenantNotFoundException(Throwable cause) {
        super(cause);
    }
}
