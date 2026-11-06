package com.schooltech.sms.entity.client.student.dto;

import com.schooltech.sms.entity.client.student.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfileDTO {
    //student fields
    private String username;
    private String parentUsername;
    private String session;
    private String parentType;
    private String fullName;
    private String admissionNo;
    private LocalDate admissionDate;
    private Student.AdmissionStatus admissionStatus;
    private String aadharNo;
    private String apaarNo;
    private String gender;
    private String bloodGroup;
    private String className;
    private Long rollNo;
    private String fatherName;
    private String motherName;
    private String guardianName;
    private LocalDate dob;
    private String medium;
    private String permanentAddress;
    private String currentAddress;
    private String ruralOrUrban;
    private String busRoute;
    private Map<String, String> documents;
    private String studentUpdatedBy; // from Student.updatedBy
    private Map<String, String> parentActions;
    //user fields
    private String email;
    private String phoneNo;
    private String altPhoneNo;
    private String updatedBy;

}
