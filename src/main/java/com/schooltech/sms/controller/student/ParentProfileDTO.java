package com.schooltech.sms.controller.student;

import com.schooltech.sms.entity.client.payment.bank.BankDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentProfileDTO {
    //parent fields
    private String username;
    private String parentType;
    private String fullName;
    private String aadharNo;
    private String panNo;
    private String gender;
    private LocalDate dob;
    private Map<String, String> documents;
    // User fields
    private String email;
    private String phoneNo;
    private String altPhoneNo;
    private String updatedBy;
    // Bank Details
    private BankDetails bankDetails;
}
