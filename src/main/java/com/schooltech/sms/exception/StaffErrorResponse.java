package com.schooltech.sms.exception;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StaffErrorResponse {

    private int status;
    private String message;
    private Long timeStamp;

    public StaffErrorResponse() {
    }

    public StaffErrorResponse(int status, String message, Long timeStamp) {
        this.status = status;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}