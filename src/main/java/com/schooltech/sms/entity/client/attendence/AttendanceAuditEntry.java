package com.schooltech.sms.entity.client.attendence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceAuditEntry {
    private String time;   // "08:02:14" — filled server-side
    private String action; // "present" / "absent" / "checkout" — filled server-side
    private String updatedBy;  // "carol danves (teacher1)" — from the request
}