package com.schooltech.sms.controller.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithAttendanceDTO {
    //student fields
    private String username;
    private String parentUsername;
    private String fatherName;
    private String fullName;
    private String className;
    private Long rollNo;
    private String gender;
    //Attendance fields
    private String status;
    private String approved;
    private String checkInTime;
    private String checkOutTime;
    ;
}
