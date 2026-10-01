package com.schooltech.sms.entity.client.circulars;


import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
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
@Table(name = "class_circular", uniqueConstraints = @UniqueConstraint(columnNames = {"className", "session"}))
public class ClassCircular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "session", nullable = false) //example 2024-2025
    private String session;
    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "school_monthly_fees", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyDoubleValueConverter.class)
    private Map<String, Double> schoolMonthlyFees;

    @Column(name = "school_misc_fees", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyDoubleValueConverter.class)
    private Map<String, Double> schoolMiscFees;

    @Column(name = "school_fees_rules", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyDoubleValueConverter.class)
    private Map<String, Double> schoolFeesRules;

    @Column(name = "subjects", columnDefinition = "JSON")
    @Convert(converter = STConverter.ListOfStringConverter.class)
    private List<String> subjects;

    @Column(name = "exam_schedules", columnDefinition = "JSON")
    @Convert(converter = STConverter.ExamScheduleConverter.class)
    private Map<String, List<ExamSchedule>> examSchedules;

    @Column(name = "exam_schedules_grade", columnDefinition = "JSON")
    @Convert(converter = STConverter.ExamScheduleGradeConverter.class)
    private Map<String, List<ExamScheduleGrade>> examSchedulesGrade;

    @Column(name = "test_schedules", columnDefinition = "JSON")
    @Convert(converter = STConverter.ExamScheduleConverter.class)
    private Map<String, List<ExamSchedule>> testSchedules;

    @Column(name = "test_schedules_grade", columnDefinition = "JSON")
    @Convert(converter = STConverter.ExamScheduleGradeConverter.class)
    private Map<String, List<ExamScheduleGrade>> testSchedulesGrade;

    @Column(name = "class_qualities", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyListOfStringValueConverter.class)
    private Map<String, List<String>> classQualities;

    @Column(name = "grade_circular", columnDefinition = "JSON")
    @Convert(converter = STConverter.GradeCircularConverter.class)
    private List<ResultGrade> gradeCircular;

    @Column(name = "shifts", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyShiftTimeValueConverter.class)
    private Map<String, ShiftTime> shifts;

    @Column(name = "date_sheet", columnDefinition = "JSON")
    @Convert(converter = STConverter.DateSheetConverter.class)
    private Map<String, List<Datesheetsubject>> dateSheet;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;


}



