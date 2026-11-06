package com.schooltech.sms.controller.student;


import com.schooltech.sms.entity.client.attendence.StudentAttendance;
import com.schooltech.sms.service.student.StudentAttendanceService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
public class StudentAttendanceController {
    private static final Logger logger = LoggerFactory.getLogger(StudentAttendanceController.class);
    @Autowired
    private StudentAttendanceService studentAttendanceService;

    @Transactional
    @PostMapping("/markAttendenceStudent")
    public ResponseEntity<String> saveStudentsAttendance(@RequestBody List<StudentAttendance> studentAttendances) {
        return studentAttendanceService.saveStudentsAttendance(studentAttendances);
    }

    @GetMapping("/getStudentAttendence/{studentId}")
    public ResponseEntity<StudentAttendance> checkforExistingAttendence(@PathVariable String studentId, @RequestParam("date") LocalDate date) {
        return studentAttendanceService.checkforExistingAttendence(studentId, date);
    }

    @GetMapping("/getStudentAttendenceHistory/{studentId}")
    public ResponseEntity<List<StudentAttendance>> getStudentAttendenceHistoryById(@PathVariable String studentId, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return studentAttendanceService.getStudentAttendenceHistoryById(studentId, startDate, endDate);
    }

    @GetMapping("/getStudentAttendencesByDateAndClass/{className}")
    public ResponseEntity<List<StudentAttendance>> getStudentAttendencesByDateAndClass(@PathVariable String className, @RequestParam("date") LocalDate date) {
        return studentAttendanceService.getStudentAttendencesByDateAndClass(className, date);
    }


    @DeleteMapping("/deleteStudentAttendancesForDate")
    public ResponseEntity<String> deleteAttendance(@RequestParam LocalDate date) {
        return studentAttendanceService.deleteAttendance(date);
    }

}