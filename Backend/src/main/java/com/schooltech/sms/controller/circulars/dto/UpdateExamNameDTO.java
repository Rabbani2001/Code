package com.schooltech.sms.controller.circulars.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExamNameDTO {
    private String oldExamName;
    private String newExamName;
}
