package com.schooltech.sms.controller.circulars.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteExamNameDTO {
    private String examName;
    private String subjectName;
}
