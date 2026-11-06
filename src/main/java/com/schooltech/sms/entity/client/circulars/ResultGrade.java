package com.schooltech.sms.entity.client.circulars;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultGrade {
    private int min;
    private int max;
    private String grade;
    private String gradeDescription;
}
