package com.schooltech.sms.controller.communication.dto;

import lombok.Data;

@Data
public class Msg91OtpVerifyResponse {
    private String message;
    private String type; // success / error
}

