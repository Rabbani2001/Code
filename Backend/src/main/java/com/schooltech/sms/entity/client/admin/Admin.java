//package com.schooltech.sms.entity.client.admin;
//
//import com.schooltech.sms.entity.BaseEntity;
//import com.schooltech.sms.utility.converters.STConverter;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotBlank;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//import org.hibernate.annotations.Where;
//
//import java.sql.Timestamp;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//
//@Entity
//@NoArgsConstructor
//@Getter
//@Setter
//@ToString
//@Table(name = "admin")
//@Where(clause = "is_active=true")
//public class Admin extends BaseEntity {
//    //define fields
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long Id;
//
//    @NotBlank(message = "Admin Username cannot be empty or null")
//    @Column(name = "username", nullable = false, unique = true)
//    private String username;   //It is uniqueID
//
//    @Column(name = "employee_id", unique = true)
//    private String employeeId;
//
//    @Column(name = "full_name", nullable = false)
//    private String fullName;
//
//    @Column(name = "gender")
//    private String gender;
//
//    @Column(name = "aadhar_no", unique = true)
//    private String aadharNo;
//
//    @Column(name = "pan_no", unique = true)
//    private String panNo;
//
//    @Column(name = "class_names", columnDefinition = "JSON")
//    @Convert(converter = STConverter.ListOfStringConverter.class)
//    private List<String> classNames;
//
//    @Column(name = "father_name")
//    private String fatherName;
//
//    @Column(name = "mother_name")
//    private String motherName;
//
//    @Column(name = "guardian_name")//It can also be treated as spouse name
//    private String guardianName;
//
//    @Column(name = "qualification")
//    private String qualification;//Med,bed
//
//    @Column(name = "designation", nullable = false)
//    private String designation;//tgt,pgt,prt
//
//    @Column(name = "dob")
//    private LocalDate dob;
//
//    @Column(name = "permanent_address")
//    private String permanentAddress;
//
//    @Column(name = "current_address")
//    private String currentAddress;
//
//    @Column(name = "joining_date")
//    private LocalDate joiningDate;
//
//    @Column(name = "bus_route")
//    private String busRoute;
//
//    @Column(name = "documents", columnDefinition = "JSON")
//    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
//    private Map<String, String> documents;
//
//    @Column(name = "timestamp", nullable = false)
//    private Timestamp timestamp;
//}
//
