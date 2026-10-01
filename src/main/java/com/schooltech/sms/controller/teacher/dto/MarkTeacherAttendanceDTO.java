package com.schooltech.sms.controller.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkTeacherAttendanceDTO {

    private String fullName;
    private String role;
}
