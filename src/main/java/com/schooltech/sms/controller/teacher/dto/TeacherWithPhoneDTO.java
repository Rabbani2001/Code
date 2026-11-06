package com.schooltech.sms.controller.teacher.dto;

import com.schooltech.sms.entity.client.teacher.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherWithPhoneDTO {
    private Teacher teacher;
    private String phoneNo;
}
