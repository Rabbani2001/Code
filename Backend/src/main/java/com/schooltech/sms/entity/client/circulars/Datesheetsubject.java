package com.schooltech.sms.entity.client.circulars;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Datesheetsubject {
    private String subjectName;
    private String date;      // "YYYY-MM-DD"
    private String startTime; // "HH:mm", 24hr — same slot for every subject in the sheet
    private String endTime;   // "HH:mm", 24hr
}
