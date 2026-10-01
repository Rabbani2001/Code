package com.schooltech.sms.service.student;


import com.schooltech.sms.dao.client.attendence.StudentAttendenceRepository;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.dao.client.student.ParentRepository;
import com.schooltech.sms.dao.client.student.StudentOtherInfoRepository;
import com.schooltech.sms.dao.client.student.StudentRepository;
import com.schooltech.sms.entity.client.attendence.StudentAttendance;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.exception.AttendenceNotFoundException;
import com.schooltech.sms.exception.dto.StudentNotFoundException;
import com.schooltech.sms.service.communication.EmailService;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentAttendanceService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private StudentOtherInfoRepository studentOtherInfoRepository;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private StudentAttendenceRepository studentAttendenceRepository;
    @Autowired
    private ClassCircularRepository classCircularRepository;

    @Transactional
    public ResponseEntity<String> saveStudentsAttendance(List<StudentAttendance> studentAttendances) {
        for (StudentAttendance studentAttendance : studentAttendances) {
            //validating student is present or not
            Optional<Student> student = studentRepository.findByUsername(studentAttendance.getUsername());
            student.orElseThrow(() -> new StudentNotFoundException("You are marking attendance for invalid Student"));
            //To Handle to update the attendance
            StudentAttendance studentAttendanceDB = studentAttendenceRepository.findStudentAttendenceByStudentIdAndDate(studentAttendance.getUsername(), studentAttendance.getDate());
            if (studentAttendanceDB != null) {
                studentAttendance.setId(studentAttendanceDB.getId());
            }
            studentAttendance.setTimestamp(DateUtility.getCurrentTimeStamp());
        }
        studentAttendenceRepository.saveAllAndFlush(studentAttendances);
        return ResponseEntity.ok("Students Attendance MARKED!!!");
    }

    public ResponseEntity<StudentAttendance> checkforExistingAttendence(String studentId, LocalDate date) {
        System.out.println(studentId + "========= " + date);
        StudentAttendance studentAttendance = studentAttendenceRepository.findStudentAttendenceByStudentIdAndDate(studentId, date);
        System.out.println(studentAttendance);
        if (studentAttendance == null) {
            throw new AttendenceNotFoundException("Attendence not Found for ID : " + studentId);
        }
        return new ResponseEntity<>(studentAttendance, HttpStatus.OK);
    }

    public ResponseEntity<List<StudentAttendance>> getStudentAttendenceHistoryById(String studentId, LocalDate startDate, LocalDate endDate) {
        List<StudentAttendance> studentAttendances = studentAttendenceRepository.findAllAttendenceBetweenDates(studentId, startDate, endDate);
        return new ResponseEntity<>(studentAttendances, HttpStatus.OK);
    }

    public ResponseEntity<List<StudentAttendance>> getStudentAttendencesByDateAndClass(String className, LocalDate date) {
        List<StudentAttendance> studentAttendances = studentAttendenceRepository.findAllAttendenceByDateAndClass(className, date);
        return new ResponseEntity<>(studentAttendances, HttpStatus.OK);
    }


    public ResponseEntity<String> deleteAttendance(LocalDate date) {
        try {
            List<StudentAttendance> studentAttendances = studentAttendenceRepository.findByDate(date);
            studentAttendances.stream().forEach((studentAttendance) -> {
                studentAttendenceRepository.delete(studentAttendance);
            });
            return new ResponseEntity<>("Student Attendances for Date: " + date + " has been deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable To Delete Student Attendances for Date: " + date, HttpStatus.NOT_FOUND);
        }
    }


}