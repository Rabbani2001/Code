package com.schooltech.sms.entity.client.student;

import com.schooltech.sms.entity.BaseEntity;
import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "student", uniqueConstraints = @UniqueConstraint(columnNames = {"rollNo", "className"}))
//@SQLDelete(sql = "UPDATE student SET is_deleted = true WHERE id=?")
@Where(clause = "is_active=true")
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank(message = "Student Username cannot be empty or null")
    @Column(name = "username", nullable = false, unique = true)
    private String username;   //It is uniqueID

    @Column(name = "parent_username", nullable = false)
    // It cannot be unique as multiple student(siblings) might have same parent_username.
    private String parentUsername;

    @Column(name = "session", nullable = false) //example 2024-2025
    private String session;

    @Column(name = "parent_type", nullable = false)
    private String parentType;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "admission_no", unique = true)
    private String admissionNo;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "admission_status", nullable = false)
    private AdmissionStatus admissionStatus;

    @Column(name = "aadhar_no", unique = true)
    private String aadharNo;

    @Column(name = "apaar_no", unique = true)
    private String apaarNo;

    @Column(name = "gender")
    private String gender;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "class_name")
    private String className;

    @Column(name = "roll_no")
    private Long rollNo;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "guardian_name")
    private String guardianName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "medium")
    private String medium;

    @Column(name = "permanent_address")
    private String permanentAddress;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "rural_or_urban")
    private String ruralOrUrban;

    @Column(name = "bus_route")
    private String busRoute;

    @Column(name = "documents", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> documents;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "parent_actions", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> parentActions; //Need to check its use ....and convert with any other solution

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    public enum AdmissionStatus {
        OLD, NEW
    }

}