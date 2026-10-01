package com.schooltech.sms.controller.communication.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipientDTO {
    private String mobile;
    private String studentName;
    private String schoolName;
    private String date;

}
