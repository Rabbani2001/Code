package com.schooltech.sms.controller.teacher.dto;

import com.schooltech.sms.entity.client.attendence.TeacherAttendance;
import com.schooltech.sms.entity.client.circulars.RoleCircular;
import com.schooltech.sms.entity.client.payment.salary.EmployeeSalary;
import com.schooltech.sms.entity.client.teacher.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherWithSalaryDTO {
    private Teacher teacher;
    private TeacherAttendance teacherAttendance;
    private EmployeeSalary employeeSalary;
    private RoleCircular roleCircular;
}
