package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.student.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPanelDTO {
    //student fields
    private String username;
    private String parentUsername;
    private String session;
    private String parentType;
    private String fullName;
    private String admissionNo;
    private Student.AdmissionStatus admissionStatus;
    private String gender;
    private String className;
    private Long rollNo;
    private LocalDate dob;
    private String aadharNo;
    private String permanentAddress;
    private String currentAddress;
    private String busRoute;
    private String bloodGroup;
    private String fatherName;
    private String motherName;
    private String guardianName;
    private Map<String, String> documents;
    //user fields
    private String email;
    private String phoneNo;
    //Validation field
    private boolean isProfileCompleted;
}
