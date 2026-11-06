package com.schooltech.sms.controller.visitor.dto;

import com.schooltech.sms.entity.client.payment.bank.BankDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorProfileDTO {
    //teacher fields
    private String username;
    private String employeeId;
    private String fullName;
    private String gender;
    private String aadharNo;
    private String fatherName;
    private LocalDate dob;
    private String permanentAddress;
    private String currentAddress;
    private Map<String, String> documents;
    // User fields
    private String email;
    private String phoneNo;
    private String altPhoneNo;
    // Bank Details
    private BankDetails bankDetails;
}
