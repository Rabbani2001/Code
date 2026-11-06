package com.schooltech.sms.controller.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentActivationRequestDTO {
    private String parentUsername;
    private String updatedBy;
}
