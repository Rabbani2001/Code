package com.schooltech.sms.controller.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkStaffAttendanceDTO {

    private String fullName;
    private String role;
}
