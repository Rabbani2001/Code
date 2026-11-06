package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.payment.StudentBusFeeReceipt;
import com.schooltech.sms.entity.client.payment.StudentFeeReceipt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusFeeReceiptsDTO {
    private String feeType;
    //StudentParent Details
    private String fullName;
    private String fatherName;
    private String className;
    //StudentReceipt
    private String username;   //It is uniqueID
    private String session;
    private String receiptNo;
    private LocalDate date;
    private String schedules;
    private StudentBusFeeReceipt.Mode mode;
    private Double total;
    private Double totalLateConcSchlr;
    private Double lateFeeCharges;
    private Double concession;
    private Map<String, Double> concessionSplit;
    private Double scholarship;
    private String remarks;
    private String collectedBy;
    private Map<String, Double> schedulesBifurcated;
}