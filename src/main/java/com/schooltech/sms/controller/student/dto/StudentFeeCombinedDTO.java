package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.payment.StudentFeeReceipt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentFeeCombinedDTO {
    private List<StudentFeeDTO> studentFeeDTOList;
    private StudentFeeReceipt studentFeeReceipt;
}
