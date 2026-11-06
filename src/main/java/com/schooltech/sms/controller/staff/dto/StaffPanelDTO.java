package com.schooltech.sms.controller.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffPanelDTO {
    //Staff fields
    private String username;
    private String fullName;
    private String role;
    private String employeeId;
    private String designation;
    private Map<String, String> documents;
    private String gender;

    // User fields
    private String phoneNo;
}
