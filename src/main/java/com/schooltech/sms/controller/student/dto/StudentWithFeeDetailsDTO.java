package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.payment.StudentFee;
import com.schooltech.sms.entity.client.student.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithFeeDetailsDTO {
    private Student student;
    private StudentFee studentFee;
    private String email;
    private String phoneNo;
}
