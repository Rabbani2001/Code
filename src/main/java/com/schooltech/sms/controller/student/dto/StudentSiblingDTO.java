package com.schooltech.sms.controller.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentSiblingDTO {
    private String fullName;
    private String fatherName;
    private String className;
    private String parentUsername;
    private String username;
    private String admissionNo;
    private String admissionStatus;
    private Long rollNo;
    private String gender;
    private Map<String, String> documents;
    private boolean isProfileCompleted;
    private String phoneNo;

}
