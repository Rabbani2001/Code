package com.schooltech.sms.controller.student;


import com.schooltech.sms.controller.student.dto.*;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.student.StudentOtherInfo;
import com.schooltech.sms.entity.client.student.dto.StudentProfileDTO;
import com.schooltech.sms.service.student.StudentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    @Autowired
    private StudentService studentService;
    @Autowired
    private ClassCircularRepository classCircularRepository;


    @GetMapping("/getStudents")
    public ResponseEntity<List<Student>> getStudents() {
        return studentService.getStudents();
    }

    @GetMapping("/getDeActivatedStudents")
    public ResponseEntity<List<Student>> getDeActivatedStudents() {
        return studentService.getDeActivatedStudents();
    }

    @DeleteMapping("/deActivateStudent/{username}")
    @Transactional
    public ResponseEntity<String> deActivateStudentByUsername(
            @PathVariable String username,
            @RequestBody StudentActivationRequestDTO request) {

        studentService.deActivateStudentByUsername(
                username,
                request.getParentUsername(),
                request.getUpdatedBy()
        );
        return ResponseEntity.ok("Student with username " + username + " has been deactivated successfully.");
    }

    @PutMapping("/activateStudent/{username}")
    @Transactional
    public ResponseEntity<String> activateStudentByUsername(
            @PathVariable String username,
            @RequestBody StudentActivationRequestDTO request) {

        studentService.activateStudentWithEntity(
                username,
                request.getParentUsername(),
                request.getUpdatedBy()
        );
        return ResponseEntity.ok("Student activated successfully.");
    }

    @DeleteMapping("/deleteStudent/{username}")
    @Transactional
    public ResponseEntity<String> deleteteStudentByUsername(@PathVariable String username, @RequestParam String parentUsername) {
        studentService.deleteStudentByUsername(username, parentUsername);
        return ResponseEntity.ok("Student with username " + username + " has been deleted successfully.");
    }

    @GetMapping("/getStudents/{username}")
    public ResponseEntity<StudentProfileDTO> getStudentByUsername(@PathVariable String username) {
        return new ResponseEntity<>(studentService.getStudentProfileDTOByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/getStudentByAdmissionNo/{admissionNo}")
    public ResponseEntity<Student> getStudentByAdmissionNo(@PathVariable String admissionNo) {
        Student student = studentService.getStudentByAdmissionNo(admissionNo);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @GetMapping("/getStudentByAadharNo/{aadharNo}")
    public ResponseEntity<Student> getStudentByAadharNo(@PathVariable String aadharNo) {
        Student student = studentService.getStudentByAadharNo(aadharNo);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @GetMapping("/isStudentAadhaarExists/{aadharNo}")
    public ResponseEntity<Boolean> isStudentAadhaarExists(@PathVariable String aadharNo) {
        return ResponseEntity.ok(studentService.isStudentAadhaarExists(aadharNo));
    }

    @GetMapping("/getStudentByApaarNo/{apaarNo}")
    public ResponseEntity<Student> getStudentByApaarNo(@PathVariable String apaarNo) {
        Student student = studentService.getStudentByApaarNo(apaarNo);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @GetMapping("/getStudentsByClass/{className}")
    public ResponseEntity<List<StudentPanelDTO>> getStudentsByClass(@PathVariable String className, @RequestParam String session) {
        return studentService.getStudentsByClass(className, session);
    }

    @GetMapping("/getStudentsWithOtherInfoByClass/{className}")
    public ResponseEntity<List<StudentWithOtherInfoDTO>> getStudentsWithOtherInfoByClass(
            @PathVariable String className,
            @RequestParam String session) {
        return studentService.getStudentsWithOtherInfoByClass(className, session);
    }

    @GetMapping("/getStudentsByFatherName/{fatherName}")
    public ResponseEntity<List<Student>> getStudentsByFatherName(@PathVariable String fatherName) {
        return studentService.getStudentsByFatherName(fatherName);
    }

    @GetMapping("/getStudentsByStudentUserId/{username}")
    public ResponseEntity<List<Student>> getStudentsByStudentUserId(@PathVariable String username) {
        return studentService.getStudentsByStudentUserId(username);
    }

//    @GetMapping("/getStudentsHasAdmissionNo/{admissionNo}")
//    public ResponseEntity<List<Student>> getStudentsHasAdmissionNo(@PathVariable String admissionNo) {
//        return studentService.getStudentsByadmissionNo(admissionNo);
//    }

    @GetMapping("/getStudentsByStudentName/{studentName}")
    public ResponseEntity<List<Student>> getStudentsByStudentName(@PathVariable String studentName) {
        return studentService.getStudentsByStudentName(studentName);
    }

    @GetMapping("/getStudentsByPhoneNo/{phoneNo}")
    public ResponseEntity<AddStudentOrSiblingDTO> getStudentsByPhoneNo(@PathVariable String phoneNo) {
        return studentService.getStudentsByPhoneNo(phoneNo);
    }

    @GetMapping("/getStudentsByPhoneNoWithBusFeeDetails/{phoneNo}")
    public ResponseEntity<List<StudentWithBusFeeDetailsDTO>> getStudentsByPhoneNoWithBusFeeDetails(@PathVariable String phoneNo, @RequestParam String session) {
        List<StudentWithBusFeeDetailsDTO> students = studentService.getStudentsByPhoneNoWithBusFeeDetails(phoneNo, session);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/getStudentsByAdmissionNoWithBusFeeDetails/{admissionNo}")
    public ResponseEntity<List<StudentWithBusFeeDetailsDTO>> getStudentsByPhoneNoWithBusFeeDetails1(@PathVariable String admissionNo, @RequestParam String session) {
        List<StudentWithBusFeeDetailsDTO> students = studentService.getStudentsByAdmissionNoWithBusFeeDetails(admissionNo, session);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/getStudentsByClassWithAttendenceStatus/{className}")
    public ResponseEntity<List<StudentWithAttendanceDTO>> getStudentsByClassWithAttendence(@PathVariable String className, @RequestParam("date") LocalDate date) {
        return studentService.getStudentsByClassWithAttendence(className, date);
    }

    @GetMapping("/getStudentsByClassWithFeeDetails/{className}")
    public ResponseEntity<List<StudentWithFeeDetailsDTO>> getStudentsByClassWithFeeDetails(
            @PathVariable String className, @RequestParam String session) {
        List<StudentWithFeeDetailsDTO> students = studentService.getStudentsByClassWithFeeDetails(className, session);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/getAllStudentsFeeOnlyDetails")
    public ResponseEntity<List<StudentWithFeeDetailsDTO>> getAllStudentsFeeOnlyDetails(
            @RequestParam String session) {

        List<StudentWithFeeDetailsDTO> studentWithFeeDetailsDTOList = new ArrayList<>();

        List<ClassCircular> classCirculars = classCircularRepository.findBySession(session);
        //fetching fee for all class in a session
        classCirculars.forEach(cc -> {
            studentWithFeeDetailsDTOList.addAll(studentService.getAllStudentsFeeOnlyDetails(cc.getClassName(), session));
        });

        return ResponseEntity.ok(studentWithFeeDetailsDTOList);
    }

    @GetMapping("/getStudentsByPhoneNoWithFeeDetails/{phoneNo}")
    public ResponseEntity<List<StudentWithFeeDetailsDTO>> getStudentsByPhoneNoWithFeeDetails(
            @PathVariable String phoneNo, @RequestParam String session) {
        List<StudentWithFeeDetailsDTO> students = studentService.getStudentsByPhoneNoWithFeeDetails(phoneNo, session);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/getStudentsByAdmissionNoWithFeeDetails/{admissionNo}")
    public ResponseEntity<List<StudentWithFeeDetailsDTO>> getStudentsByAdmissionNoWithFeeDetails(
            @PathVariable String admissionNo, @RequestParam String session) {
        List<StudentWithFeeDetailsDTO> students = studentService.getStudentsByAdmissionNoWithFeeDetails(admissionNo, session);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/getStudentsByClassWithBusFeeDetails/{className}")
    public ResponseEntity<List<StudentWithBusFeeDetailsDTO>> getStudentsByClassWithBusFeeDetails(
            @PathVariable String className, @RequestParam String session) {
        List<StudentWithBusFeeDetailsDTO> students = studentService.getStudentsByClassWithBusFeeDetails(className, session);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/getAllStudentsBusFeeOnlyDetails")
    public ResponseEntity<List<StudentWithBusFeeDetailsDTO>> getAllStudentsBusFeeOnlyDetails(
            @RequestParam String session) {

        List<StudentWithBusFeeDetailsDTO> studentWithBusFeeDetailsDTOList = new ArrayList<>();

        List<ClassCircular> classCirculars = classCircularRepository.findBySession(session);
        classCirculars.forEach(cc -> {
            studentWithBusFeeDetailsDTOList.addAll(studentService.getAllStudentsBusFeeOnlyDetails(cc.getClassName(), session));
        });

        return ResponseEntity.ok(studentWithBusFeeDetailsDTOList);
    }

    @GetMapping("/getStudentFeeStatus/{username}")
    public ResponseEntity<Boolean> getStudentFeeStatus(
            @PathVariable String username,
            @RequestParam String session) {
        return ResponseEntity.ok(studentService.getStudentFeeStatus(username, session));
    }

    @GetMapping("/getStudentsByClassWithExamSchedule/{className}")
    public ResponseEntity<List<StudentWithExamScheduleDTO>> getStudentsWithExamSchedule(
            @PathVariable String className, @RequestParam String session) {
        List<StudentWithExamScheduleDTO> students = studentService.getStudentsWithExamSchedule(className, session);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/getStudentsByParent/{username}")
    public ResponseEntity<List<Student>> getStudentsByParent(@PathVariable String username) {
        return studentService.getStudentsByParent(username);
    }

    @PostMapping("/saveStudentWithEntity")
    @Transactional
    public ResponseEntity<String> saveStudentWithEntity(@Valid @RequestBody List<StudentParentUserDTO> studentParentUserDTOList) {
        return studentService.saveStudentWithAssociatedEntity(studentParentUserDTOList);
    }

    @PostMapping("/saveStudents")
    @Transactional
    public ResponseEntity<String> saveStudents(@Valid @RequestBody List<@Valid Student> students) {
        return studentService.saveStudents(students);
    }

    @PutMapping("/updateStudents")
    @Transactional
    public ResponseEntity<String> updateStudents(@Valid @RequestBody List<@Valid Student> students) {
        return studentService.updateStudents(students);
    }

    @PutMapping("/updateStudentsByAadharNo")
    @Transactional
    public ResponseEntity<String> updateStudentsByAadharNo(@RequestBody List<Student> students) {
        return studentService.updateStudentsByAadharNo(students);
    }

    @PutMapping("/promoteStudents")
    @Transactional
    public ResponseEntity<String> promoteStudents(@Valid @RequestBody List<@Valid Student> students) {
        return studentService.promoteStudents(students);
    }


    ///////////////////// Student OtherInfo ////////////////////////////

    @GetMapping("/getStudentOtherinfo/{username}")
    public ResponseEntity<StudentOtherInfo> getStudentOtherinfo(@PathVariable String username) {
        return studentService.getStudentsInfo(username);
    }

    @PostMapping("/saveStudentOtherInfo")
    @Transactional
    public ResponseEntity<String> saveStudentOtherInfo(@Valid @RequestBody List<@Valid StudentOtherInfo> studentOtherInfos) {
        return studentService.saveStudentOtherInfo(studentOtherInfos);
    }

    @PutMapping("/updateStudentOtherInfo")
    @Transactional
    public ResponseEntity<String> updateStudentOtherInfo(
            @Valid @RequestBody List<@Valid StudentOtherInfo> studentOtherInfos,
            @RequestParam String updatedBy) {

        return studentService.updateStudentOtherInfo(studentOtherInfos, updatedBy);
    }

}