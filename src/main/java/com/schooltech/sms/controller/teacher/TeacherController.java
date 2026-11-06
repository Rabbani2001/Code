package com.schooltech.sms.controller.teacher;


import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.controller.teacher.dto.*;
import com.schooltech.sms.entity.client.teacher.Teacher;
import com.schooltech.sms.service.teacher.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private AuthService authService;

    @GetMapping("/getTeachers")
    public ResponseEntity<List<TeacherWithPhoneDTO>> getTeachers() {
        return teacherService.getTeachers();
    }

    @GetMapping("/getDeActivatedTeachers")
    public ResponseEntity<List<Teacher>> getDeActivatedTeachers() {
        return teacherService.getDeActivatedTeachers();
    }

    @DeleteMapping("/deActivateTeacher/{username}")
    @Transactional
    public ResponseEntity<String> deActivateTeacherByUsername(
            @PathVariable String username,
            @RequestBody TeacherDeactivationRequestDTO request) {

        teacherService.deActivateTeacherWithEntity(username, request.getUpdatedBy());
        return ResponseEntity.ok("Teacher with username " + username + " has been deactivated successfully.");
    }

    @PutMapping("/activateTeacher/{username}")
    @Transactional
    public ResponseEntity<String> activateTeacherByUsername(
            @PathVariable String username,
            @RequestBody TeacherDeactivationRequestDTO request) {
        
        teacherService.activateTeacherWithEntity(username, request.getUpdatedBy());
        return ResponseEntity.ok("Teacher with username " + username + " has been activated successfully.");
    }

    @DeleteMapping("/deleteTeacher/{username}")
    @Transactional
    public ResponseEntity<String> deleteTeacherByUsername(@PathVariable String username) {
        teacherService.deleteTeacherWithEntity(username);
        return ResponseEntity.ok("teacher with username " + username + " has been deleted successfully.");
    }

    @GetMapping("/getTeachersWithAttendanceStatus")
    public ResponseEntity<List<TeacherWithAttendanceDTO>> getTeachersWithAttendanceStatus(@RequestParam("date") LocalDate date) {
        return teacherService.getTeachersWithAttendenceStatus(date);
    }


    @GetMapping("/getTeachersWithSalary")
    public ResponseEntity<List<TeacherWithSalaryDTO>> getTeachersWithSalary() {
        return teacherService.getTeachersWithSalary();
    }

    @GetMapping("/getTeacherByUsername/{teacherId}")
    public ResponseEntity<TeacherProfileDTO> getTeacherByUsername(@PathVariable String teacherId) {
        return new ResponseEntity<>(teacherService.getTeacherProfileDTOByUsername(teacherId), HttpStatus.OK);
    }

    @GetMapping("/isEmployeeAadhaarExists/{aadharNo}")
    public ResponseEntity<Boolean> isEmployeeAadhaarExists(@PathVariable String aadharNo) {
        return ResponseEntity.ok(teacherService.isEmployeeAadhaarExists(aadharNo));
    }

    @GetMapping("/isEmployeeIdExist/{employeeId}")
    public ResponseEntity<Boolean> isEmployeeIdExist(@PathVariable String employeeId) {
        return ResponseEntity.ok(teacherService.isEmployeeIdExist(employeeId));
    }

//    @GetMapping("/getTeacherByEmail/{emailId}")
//    public ResponseEntity<Teacher> getTeacherByEmailId(@PathVariable String emailId) {
//        Teacher teacher = teacherService.getTeacherByEmailId(emailId);
//        return new ResponseEntity<>(teacher, HttpStatus.OK);
//    }

//    @GetMapping("/getTeacherByPhoneNo/{phoneNo}")
//    public ResponseEntity<Teacher> getTeacherByPhoneNo(@PathVariable String phoneNo) {
//        Teacher teacher = teacherService.getTeacherByPhoneNo(phoneNo);
//        return new ResponseEntity<>(teacher, HttpStatus.OK);
//    }

    /*
     * below method will add entry in teacher, user table
     */
    @PostMapping("/saveTeachersWithEntity")
    public ResponseEntity<String> saveTeachersWithEntity(@RequestBody List<TeacherUserDTO> teacherUserDTOList) {
        return teacherService.saveTeachersWithEntity(teacherUserDTOList);
    }

    @PostMapping("/saveTeachers")
    public ResponseEntity<String> saveTeachers(@Valid @RequestBody List<@Valid Teacher> teacherList) {
        return teacherService.saveTeachers(teacherList);
    }

    @PutMapping("/updateTeachers")
    @Transactional
    public ResponseEntity<String> updateTeachers(@Valid @RequestBody List<@Valid Teacher> teacherList) {
        return teacherService.updateTeachers(teacherList);
    }

}