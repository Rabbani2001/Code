package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.student.Parent;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.student.StudentOtherInfo;
import com.schooltech.sms.entity.client.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentParentUserDTO {
    private User user;
    private Parent parent;
    private List<Student> studentList;
    private List<StudentOtherInfo> studentOtherInfoList;
}
