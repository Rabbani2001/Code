package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.student.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithOtherInfoDTO {

    // Student + User fields
    private String username;
    private String fullName;
    private String session;
    private String gender;
    private String bloodGroup;
    private String apaarNo;
    private String parentType;
    private String fatherName;
    private String motherName;
    private String guardianName;
    private Student.AdmissionStatus admissionStatus;
    private String admissionNo;
    private LocalDate admissionDate;
    private String className;
    private Long rollNo;
    private LocalDate dob;
    private String medium;
    private String aadharNo;
    private String permanentAddress;
    private String currentAddress;
    private String ruralOrUrban;
    private String busRoute;

    // StudentOtherInfo fields
    private String familyId;
    private LocalDate fatherDob;
    private String fatherAadharNo;
    private String fatherOccupation;
    private String fatherEducation;
    private LocalDate motherDob;
    private String motherAadharNo;
    private String motherOccupation;
    private String motherEducation;
    private LocalDate guardianDob;
    private String guardianAadharNo;
    private String guardianOccupation;
    private String guardianEducation;
    private String guardianGender;
    private String religion;
    private String caste;
    private String nationality;
    private String domicile;
    private String isMinority;
    private String minorityCategory;
    private String isBPL;
    private String stream;
    private String subjectsOpted;
    private String subjectOptional;
    private String previousSchoolName;
    private String previousAdmissionNo;
    private String previousSchoolCode;
    private String previousLeavingDate;
    private String previousClassPassed;
    private String previousMarksObtained;

    // user fields
    private String email;
    private String phoneNo;
    private String altPhoneNo;
}