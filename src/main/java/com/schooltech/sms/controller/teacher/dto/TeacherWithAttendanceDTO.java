package com.schooltech.sms.controller.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherWithAttendanceDTO {
    // Teacher fields
    private String username;
    private String fullName;
    private String employeeId;
    private String gender;
    private List<String> classNames;
    private String role;
    private Map<String, String> documents; // for atten profile photo
    //attendance fields
    private String status;
    private String approved;
    private String checkInTime;
    private String checkOutTime;
}
