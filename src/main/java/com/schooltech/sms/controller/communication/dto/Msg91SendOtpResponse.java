package com.schooltech.sms.controller.communication.dto;

import lombok.Data;

@Data
public class Msg91SendOtpResponse {
    private String request_id;
    private String type; // success / error
}

