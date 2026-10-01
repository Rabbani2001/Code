package com.schooltech.sms.controller.visitor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorPanelDTO {
    //Staff fields
    private String username;
    private String fullName;
    private String employeeId;
    private Map<String, String> documents;
    // User fields
    private String phoneNo;
}
