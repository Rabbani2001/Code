package com.schooltech.sms.controller.teacher.dto;

import com.schooltech.sms.entity.client.teacher.Teacher;
import com.schooltech.sms.entity.client.user.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TeacherUserDTO {
    private Teacher teacher;
    private User user;
}
