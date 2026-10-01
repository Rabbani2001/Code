package com.schooltech.sms.controller.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteStudentFeeReceiptRequestDTO {
    private String username;
    private String session;
    private String deletedBy;
}
