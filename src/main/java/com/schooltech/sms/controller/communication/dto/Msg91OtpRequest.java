package com.schooltech.sms.controller.communication.dto;

import lombok.Data;

import java.util.Map;

@Data
public class Msg91OtpRequest {
    private Map<String, String> variables;
}
