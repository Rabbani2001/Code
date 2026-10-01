package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.payment.StudentCombineFeeStats;
import com.schooltech.sms.entity.client.payment.StudentFee;
import com.schooltech.sms.entity.client.payment.StudentFeeReceipt;
import com.schooltech.sms.entity.client.payment.StudentFeeStats;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentFeeDTO {
    private StudentFee studentFee;
    private String className;
    private StudentFeeReceipt studentFeeReceipt;
    private StudentFeeStats studentFeeStats;
    private StudentCombineFeeStats studentCombineFeeStats;
}
