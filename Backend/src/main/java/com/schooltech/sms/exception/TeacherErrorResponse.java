package com.schooltech.sms.exception;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeacherErrorResponse {

    private int status;
    private String message;
    private Long timeStamp;

    public TeacherErrorResponse() {
    }

    public TeacherErrorResponse(int status, String message, Long timeStamp) {
        this.status = status;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
