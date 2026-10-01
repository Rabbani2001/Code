package com.schooltech.sms.entity.client.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamSchedule {
    private String subjectName;
    private Integer  marksObtained;
    private String date;
}
