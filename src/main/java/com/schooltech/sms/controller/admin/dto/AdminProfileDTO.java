package com.schooltech.sms.controller.admin.dto;

import com.schooltech.sms.entity.client.payment.bank.BankDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminProfileDTO {

    // Admin fields
    private String username;
    private String employeeId;
    private String altPhoneNo;
    private String fullName;
    private String gender;
    private String aadharNo;
    private String panNo;
    private List<String> classNames;
    private String fatherName;
    private String motherName;
    private String guardianName;
    private String qualification;
    private String designation;
    private LocalDate dob;
    private String permanentAddress;
    private String currentAddress;
    private LocalDate joiningDate;
    private String busRoute;
    private Map<String, String> documents;
    // User fields
    private String email;
    private String phoneNo;
    // Bank Details
    private BankDetails bankDetails;
}
