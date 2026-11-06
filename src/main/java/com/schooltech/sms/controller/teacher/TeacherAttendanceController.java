package com.schooltech.sms.controller.teacher;


import com.schooltech.sms.controller.teacher.dto.MarkTeacherAttendanceDTO;
import com.schooltech.sms.entity.client.attendence.TeacherAttendance;
import com.schooltech.sms.service.teacher.TeacherAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
public class TeacherAttendanceController {
    @Autowired
    private TeacherAttendanceService teacherAttendanceService;

    @GetMapping("/getTeacherAttendence/{teacherId}")
    public ResponseEntity<TeacherAttendance> getTeacherAttendence(@PathVariable String teacherId, @RequestParam("date") LocalDate date) {
        return teacherAttendanceService.getTeacherAttendence(teacherId, date);
    }

    @GetMapping("/getTeacherAttendenceHistory/{teacherId}")
    public ResponseEntity<List<TeacherAttendance>> getTeacherAttendenceHistory(@PathVariable String teacherId, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return teacherAttendanceService.getTeacherAttendenceHistory(teacherId, startDate, endDate);
    }


    @GetMapping("/getTeacherAttendencesByDate/{date}")
    public ResponseEntity<List<TeacherAttendance>> getTeacherAttendencesByDate(@PathVariable LocalDate date) {
        return teacherAttendanceService.getTeacherAttendencesByDate(date);
    }

    @DeleteMapping("/deleteTeacherAttendancesForDate")
    public ResponseEntity<String> deleteTeacherAttendancesForDate(@RequestParam LocalDate date) {
        return teacherAttendanceService.deleteTeacherAttendancesForDate(date);
    }

    @Transactional  //transaction is needed as it is post api call
    @PostMapping("/markTeacherAttendance")
    public ResponseEntity<List<MarkTeacherAttendanceDTO>> markTeacherAttendance(@RequestBody List<TeacherAttendance> teacherAttendances) {  //sdn one object if attendence to be marked for single teacher
        return teacherAttendanceService.markAttendenceTeacher(teacherAttendances);
    }


    @PutMapping("/updateTeachersAttendance")
    @Transactional
    public ResponseEntity<String> updateTeachersAttendance(@RequestBody List<TeacherAttendance> teacherAttendanceList) {
        return teacherAttendanceService.updateTeachersAttendance(teacherAttendanceList);
    }
}