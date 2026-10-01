package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.circulars.ClassQuality;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.student.StudentExamSchedule;
import com.schooltech.sms.entity.client.student.StudentExamScheduleGrade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithExamScheduleDTO {
    private Student student;
    private Map<String, List<StudentExamSchedule>> examSchedules;
    private Map<String, List<StudentExamScheduleGrade>> examSchedulesGrade;
    private Map<String, List<StudentExamSchedule>> testSchedules;
    private Map<String, List<StudentExamScheduleGrade>> testSchedulesGrade;
    private Map<String, String> examScheduleComments;
    private Map<String, String> testScheduleComments;
    private String examRemarks;
    private String resultId;
    private String session;
    private Map<String, List<ClassQuality>> classQualities;

}
