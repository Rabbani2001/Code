package com.schooltech.sms.service.circular;


import com.schooltech.sms.controller.student.dto.StudentPanelDTO;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.dao.client.payment.StudentFeeRepository;
import com.schooltech.sms.dao.client.student.StudentRepository;
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.payment.StudentFee;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.exception.CircularNotFoundException;
import com.schooltech.sms.service.staff.StaffService;
import com.schooltech.sms.service.student.StudentFeeService;
import com.schooltech.sms.service.student.StudentService;
import com.schooltech.sms.service.teacher.TeacherService;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ClassCircularService {
    @Autowired
    private ClassCircularRepository classCircularRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private StudentFeeRepository studentFeeRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentFeeService studentFeeService;

    public ResponseEntity<List<ClassCircular>> getClassCirculars() {
        List<ClassCircular> classCirculars = classCircularRepository.findAll();
        return new ResponseEntity<>(classCirculars, HttpStatus.OK);
    }

    public ResponseEntity<List<ClassCircular>> getClassCircularsBySession(String session) {
        List<ClassCircular> classCirculars = classCircularRepository.findBySession(session);
        return new ResponseEntity<>(classCirculars, HttpStatus.OK);
    }

    public ResponseEntity<List<ClassCircular>> getClassCircularsByClassNames(String classNames, String session) {
        List<String> classNameList =
                Arrays.stream(classNames.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

        List<ClassCircular> classCirculars =
                classCircularRepository.findByClassNameInAndSession(classNameList, session);
        if (classCirculars.isEmpty()) {
            throw new CircularNotFoundException("No Class Circular found for given classes");
        }

        return new ResponseEntity<>(classCirculars, HttpStatus.OK);
    }

    public ResponseEntity<Boolean> isClassLinked(String className, String session) {
        if (studentService.isAnyStudentPresentInClass(className, session)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        if (staffService.isAnyStaffPresentInClass(className)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        if (teacherService.isAnyTeacherPresentInClass(className)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }


    public ResponseEntity<String> saveClassCirculars(List<ClassCircular> classCirculars) {
        classCirculars.forEach(classCircular -> {
            Optional<ClassCircular> classCircularOptional =
                    classCircularRepository.findByClassNameAndSession(classCircular.getClassName(), classCircular.getSession());
            classCircularOptional.ifPresentOrElse(existingCircular -> {
                throw new RuntimeException("Class Circular: " + classCircular.getClassName() + ":" + classCircular.getSession() + " already Present. Aborting Save");
            }, () -> {
                classCircular.setTimestamp(DateUtility.getCurrentTimeStamp());
                classCircularRepository.save(classCircular);
            });
        });
        return ResponseEntity.ok("Class Circular Data Saved");
    }

    public ResponseEntity<String> updateClassCirculars(List<ClassCircular> classCirculars) {
        classCirculars.forEach(classCircular -> {
            Optional<ClassCircular> classCircularOptional =
                    classCircularRepository.findByClassNameAndSession(classCircular.getClassName(), classCircular.getSession());
            classCircularOptional.ifPresentOrElse(existingCircular -> {
                updateNonNullFields(existingCircular, classCircular);
                existingCircular.setTimestamp(DateUtility.getCurrentTimeStamp());
                classCircularRepository.save(existingCircular);
            }, () -> {
                throw new CircularNotFoundException("Class Circular: " + classCircular.getClassName() + ":" + classCircular.getSession() + " not present. Aborting Update");
            });
        });
        return ResponseEntity.ok("Class Circular Data Updated");
    }

    private void updateNonNullFields(ClassCircular existingCircular, ClassCircular newCircular) {
//        if (newCircular.getRegistrationFee() != null)
//            existingCircular.setRegistrationFee(newCircular.getRegistrationFee());
//        if (newCircular.getMonthlyFee() != null) existingCircular.setMonthlyFee(newCircular.getMonthlyFee());
//        if (newCircular.getAnnualFee() != null) existingCircular.setAnnualFee(newCircular.getAnnualFee());
//        if (newCircular.getAdmissionFee() != null) existingCircular.setAdmissionFee(newCircular.getAdmissionFee());
//        if (newCircular.getExamFee1() != null) existingCircular.setExamFee1(newCircular.getExamFee1());
//        if (newCircular.getExamFee2() != null) existingCircular.setExamFee2(newCircular.getExamFee2());
//        if (newCircular.getLateFeeDayCharges() != null)
//            existingCircular.setLateFeeDayCharges(newCircular.getLateFeeDayCharges());
//        if (newCircular.getDueStartDate() != null) existingCircular.setDueStartDate(newCircular.getDueStartDate());
//        if (newCircular.getComputerFee() != null) existingCircular.setComputerFee(newCircular.getComputerFee());
        if (newCircular.getSchoolMonthlyFees() != null)
            existingCircular.setSchoolMonthlyFees(newCircular.getSchoolMonthlyFees());
        if (newCircular.getSchoolMiscFees() != null)
            existingCircular.setSchoolMiscFees(newCircular.getSchoolMiscFees());
        if (newCircular.getSchoolFeesRules() != null)
            existingCircular.setSchoolFeesRules(newCircular.getSchoolFeesRules());
        if (newCircular.getSubjects() != null) existingCircular.setSubjects(newCircular.getSubjects());
        if (newCircular.getExamSchedules() != null) existingCircular.setExamSchedules(newCircular.getExamSchedules());
        if (newCircular.getExamSchedulesGrade() != null)
            existingCircular.setExamSchedulesGrade(newCircular.getExamSchedulesGrade());
        if (newCircular.getTestSchedules() != null)
            existingCircular.setTestSchedules(newCircular.getTestSchedules());
        if (newCircular.getTestSchedulesGrade() != null)
            existingCircular.setTestSchedulesGrade(newCircular.getTestSchedulesGrade());
        if (newCircular.getClassQualities() != null)
            existingCircular.setClassQualities(newCircular.getClassQualities());
        if (newCircular.getGradeCircular() != null)
            existingCircular.setGradeCircular(newCircular.getGradeCircular());
        if (newCircular.getDateSheet() != null)
            existingCircular.setDateSheet(newCircular.getDateSheet());
        if (newCircular.getShifts() != null) existingCircular.setShifts(newCircular.getShifts());
    }

    public void updateAssociatedFees(List<ClassCircular> classCirculars) {
        classCirculars.forEach(classCircular -> {
            if (!(classCircular.getSchoolMonthlyFees() != null && classCircular.getSchoolMiscFees() != null)) {
                return;
            }
            Optional<ClassCircular> classCircularOptional =
                    classCircularRepository.findByClassNameAndSession(classCircular.getClassName(), classCircular.getSession());
            classCircularOptional.ifPresentOrElse(existingCircular -> {
                String className = existingCircular.getClassName();
                //session passed n below calls will be checked later
                List<StudentPanelDTO> studentPanelDTOS = studentService.getStudentsByClass(className, existingCircular.getSession()).getBody();
                List<StudentFee> studentFeeList = new ArrayList<>();
                studentPanelDTOS.forEach(studentPanelDTO -> {
                    StudentFee studentFeeDB = studentFeeRepository.findByStudentIdAndSession(studentPanelDTO.getUsername(), existingCircular.getSession());
                    StudentFee studentFee = new StudentFee();
                    if (studentFeeDB != null) {
                        //check that student exist or not
                        Optional<Student> studentOptional = studentRepository.findByUsername(studentFeeDB.getUsername());
                        Student student = studentOptional.orElseThrow(() -> new RuntimeException("Student: " + studentFeeDB.getUsername() + " Not Found"));
                        studentFee.setSession(classCircular.getSession());
                        studentFee.setUsername(studentPanelDTO.getUsername());
                        studentFeeService.processStudentFeeExistingData(studentFee, studentFeeDB, classCircular, studentFeeList, student.getAdmissionStatus());
                    }
                });
                studentFeeRepository.saveAll(studentFeeList);
            }, () -> {
                throw new CircularNotFoundException("Class Circular: " + classCircular.getClassName() + ":" + classCircular.getSession() + " not present. Aborting Update associated fees");
            });
        });
        //return ResponseEntity.ok("Class Circular Associated Fees Data Updated");
    }

    @Transactional
    public ResponseEntity<String> deleteClassCirculars(String className, String session) {
        if (!classCircularRepository.existsByClassNameAndSession(className, session)) {
            throw new CircularNotFoundException("Class Circular Not Available for Class: " + className + " - " + session);
        }
        classCircularRepository.deleteByClassNameAndSession(className, session);
        return new ResponseEntity<>("Class Circular has been deleted for Class: " + className + " - " + session, HttpStatus.OK);
    }
}
