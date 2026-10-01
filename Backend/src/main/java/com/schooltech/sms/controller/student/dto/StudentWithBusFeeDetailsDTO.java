package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.payment.StudentBusFee;
import com.schooltech.sms.entity.client.student.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithBusFeeDetailsDTO {
    private Student student;
    private StudentBusFee studentBusFee;
    private String email;
    private String phoneNo;
}
