package com.schooltech.sms.entity.client.circulars;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSchedule {
    private String subjectName;
    private int maxMarks;
    private String date;
    private String startTime; // optional, "HH:mm" 24hr format, e.g. "10:30"
    private String endTime;   // optional, "HH:mm" 24hr format, e.g. "13:30"
}
