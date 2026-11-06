package com.schooltech.sms.entity.client.student;


import com.schooltech.sms.entity.client.circulars.ClassQuality;
import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "student_exam", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "session"}))
public class StudentExams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "session", nullable = false) //example 2024-2025
    private String session;
    @NotBlank(message = "Student Username cannot be empty or null")
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "result_id", nullable = false, unique = true)
    private String resultId;
    @Column(name = "class_name")
    private String className;
    @Column(name = "roll_no")
    private Long rollNo;

    @Column(name = "exam_schedules", columnDefinition = "JSON")
    @Convert(converter = STConverter.StudentExamScheduleConverter.class)
    private Map<String, List<StudentExamSchedule>> examSchedules;

    @Column(name = "exam_schedules_grade", columnDefinition = "JSON")
    @Convert(converter = STConverter.StudentExamScheduleGradeConverter.class)
    private Map<String, List<StudentExamScheduleGrade>> examSchedulesGrade;

    @Column(name = "test_schedules", columnDefinition = "JSON")
    @Convert(converter = STConverter.StudentExamScheduleConverter.class)
    private Map<String, List<StudentExamSchedule>> testSchedules;

    @Column(name = "test_schedules_grade", columnDefinition = "JSON")
    @Convert(converter = STConverter.StudentExamScheduleGradeConverter.class)
    private Map<String, List<StudentExamScheduleGrade>> testSchedulesGrade;

    @Column(name = "exam_schedule_comments", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> examScheduleComments;

    @Column(name = "test_schedule_comments", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> testScheduleComments;

    @Column(name = "exam_remarks")
    private String examRemarks;

    @Valid
    @Column(name = "class_qualities", columnDefinition = "JSON")
    @Convert(converter = STConverter.ClassQualitiesConverter.class)
    private Map<String, List<@Valid ClassQuality>> classQualities;


    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;


}



