package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.payment.StudentBusFee;
import com.schooltech.sms.entity.client.payment.StudentBusFeeReceipt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentBusFeeDTO {
    private StudentBusFee studentBusFee;
    private String busRoute;
    private StudentBusFeeReceipt studentBusFeeReceipt;
}
