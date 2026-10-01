package com.schooltech.sms.entity.client.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamScheduleGrade {
    private String subjectName;
    private String grade;
    private String date;
}
