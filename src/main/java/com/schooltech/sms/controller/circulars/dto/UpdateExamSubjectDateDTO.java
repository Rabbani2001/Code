package com.schooltech.sms.controller.circulars.dto;

import com.schooltech.sms.entity.client.circulars.ExamSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExamSubjectDateDTO {
    private String examName;
    private ExamSchedule examSchedule;
}
