package com.schooltech.sms.entity.client.student;

import com.schooltech.sms.entity.BaseEntity;
import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "parent")
@Where(clause = "is_active=true")
public class Parent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank(message = "Parent Username cannot be empty or null")
    @Column(name = "username", nullable = false, unique = true)
    private String username;   //It is uniqueID

    @Column(name = "parent_type", nullable = false)
    private String parentType;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "aadhar_no", unique = true)
    private String aadharNo;

    @Column(name = "pan_no", unique = true)
    private String panNo;

    @Column(name = "gender")
    private String gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "documents", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> documents;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

}