package com.schooltech.sms.entity.client.student;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "student_other_info")
public class StudentOtherInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;   //It is uniqueID

    @Column(name = "family_id")
    private String familyId;

    @Column(name = "father_dob")
    private LocalDate fatherDob;

    @Column(name = "father_aadhar_no", unique = true)
    private String fatherAadharNo;

    @Column(name = "father_occupation")
    private String fatherOccupation;

    @Column(name = "father_education")
    private String fatherEducation;

    @Column(name = "mother_dob")
    private LocalDate motherDob;

    @Column(name = "mother_aadhar_no", unique = true)
    private String motherAadharNo;

    @Column(name = "mother_occupation")
    private String motherOccupation;

    @Column(name = "mother_education")
    private String motherEducation;

    @Column(name = "guardian_dob")
    private LocalDate guardianDob;

    @Column(name = "guardian_aadhar_no", unique = true)
    private String guardianAadharNo;

    @Column(name = "guardian_occupation")
    private String guardianOccupation;

    @Column(name = "guardian_education")
    private String guardianEducation;

    @Column(name = "guardian_gender")
    private String guardianGender;

    @Column(name = "religion")
    private String religion;

    @Column(name = "caste")
    private String caste;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "domicile")
    private String domicile;

    @Column(name = "is_minority")
    private String isMinority;

    @Column(name = "minority_category")
    private String minorityCategory;

    @Column(name = "is_bpl")
    private String isBPL;

    @Column(name = "stream")
    private String stream;

    @Column(name = "subjects_opted")
    private String subjectsOpted;

    @Column(name = "subjects_optional")
    private String subjectOptional;

    @Column(name = "previous_school_name")
    private String previousSchoolName;

    @Column(name = "previous_admission_no")
    private String previousAdmissionNo;

    @Column(name = "previous_school_code")
    private String previousSchoolCode;

    @Column(name = "previous_leaving_date")
    private String previousLeavingDate;

    @Column(name = "previous_class_passed")
    private String previousClassPassed;

    @Column(name = "previous_marks_obtained")
    private String previousMarksObtained;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

}