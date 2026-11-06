package com.schooltech.sms.controller.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPanelDTO {
    //Admin fields
    private String username;
    private String fullName;
    private String employeeId;
    private String designation;
    private Map<String, String> documents;

    // User fields
    private String phoneNo;
}
