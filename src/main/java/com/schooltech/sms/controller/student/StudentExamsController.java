package com.schooltech.sms.controller.student;


import com.schooltech.sms.entity.client.student.StudentExams;
import com.schooltech.sms.service.student.StudentExamsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentExamsController {

    private final StudentExamsService studentExamsService;

    public StudentExamsController(StudentExamsService service) {
        this.studentExamsService = service;
    }

    @PostMapping("/saveStudentExams")
    public ResponseEntity<String> saveStudentExams(@Valid @RequestBody List<StudentExams> studentExamsList) {
        return studentExamsService.saveStudentExams(studentExamsList);
    }
//    @PutMapping("/updateStudentExams")
//    public ResponseEntity<String> updateStudentExams(@RequestBody List<StudentExams> studentExamsList) {
//        return service.updateStudentExams(studentExamsList);
//    }


    /**
     * ✅ GET all
     */
    @GetMapping("/getStudentExams")
    public ResponseEntity<List<StudentExams>> getAll() {
        return ResponseEntity.ok(studentExamsService.getAll());
    }


    /**
     * ✅ GET by Username
     */
    @GetMapping("/getStudentExams/{username}")
    public ResponseEntity<StudentExams> getByUsernameAndSession(@PathVariable String username, @RequestParam String session) {
        return ResponseEntity.ok(studentExamsService.getByUsernameAndSession(username, session));
    }

    @GetMapping("/hasMarksInExam")
    public ResponseEntity<Boolean> hasMarksInExam(
            @RequestParam String className, @RequestParam String session, @RequestParam String examName) {
        return studentExamsService.hasMarksInExam(className, session, examName);
    }

    @GetMapping("/hasMarksInTestExam")
    public ResponseEntity<Boolean> hasMarksInTestExam(
            @RequestParam String className, @RequestParam String session, @RequestParam String examName) {
        return studentExamsService.hasMarksInTestExam(className, session, examName);
    }

    @GetMapping("/hasGradesInExam")
    public ResponseEntity<Boolean> hasGradesInExam(
            @RequestParam String className, @RequestParam String session, @RequestParam String examName) {
        return studentExamsService.hasGradesInExam(className, session, examName);
    }

    @GetMapping("/hasGradesInTestExam")
    public ResponseEntity<Boolean> hasGradesInTestExam(
            @RequestParam String className, @RequestParam String session, @RequestParam String examName) {
        return studentExamsService.hasGradesInTestExam(className, session, examName);
    }

    /**
     * ✅ DELETE by ID
     */
    @DeleteMapping("/deleteStudentExams/{username}")
    public ResponseEntity<String> deleteById(@PathVariable String username) {
        studentExamsService.deleteByUsername(username);
        return ResponseEntity.ok("Deleted StudentExam with username: " + username);
    }
}
