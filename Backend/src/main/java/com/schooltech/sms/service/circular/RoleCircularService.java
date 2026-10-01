package com.schooltech.sms.service.circular;


import com.schooltech.sms.dao.client.circulars.RoleCircularRepository;
import com.schooltech.sms.dao.client.payment.StudentFeeRepository;
import com.schooltech.sms.entity.client.circulars.RoleCircular;
import com.schooltech.sms.exception.CircularNotFoundException;
import com.schooltech.sms.service.student.StudentFeeService;
import com.schooltech.sms.service.student.StudentService;
import com.schooltech.sms.service.teacher.TeacherService;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleCircularService {
    @Autowired
    private RoleCircularRepository roleCircularRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    //    @Autowired
//    private AdminService adminService;
    @Autowired
    private StudentFeeRepository studentFeeRepository;
    @Autowired
    private StudentFeeService studentFeeService;

//    public ResponseEntity<List<RoleCircular>> getRoleCirculars() {
//        List<RoleCircular> roleCirculars = roleCircularRepository.findAll();
//        return new ResponseEntity<>(roleCirculars, HttpStatus.OK);
//    }

    public ResponseEntity<List<RoleCircular>> getRoleCircularsBySession(String session) {
        List<RoleCircular> roleCirculars = roleCircularRepository.findBySession(session);
        return new ResponseEntity<>(roleCirculars, HttpStatus.OK);
    }

//    public ResponseEntity<List<ClassCircular>> getClassCircularsByClassNames(String classNames, String session) {
//        List<String> classNameList =
//                Arrays.stream(classNames.split(","))
//                        .map(String::trim)
//                        .filter(s -> !s.isEmpty())
//                        .toList();
//
//        List<ClassCircular> classCirculars =
//                roleCircularRepository.findByClassNameInAndSession(classNameList, session);
//        if (classCirculars.isEmpty()) {
//            throw new CircularNotFoundException("No Class Circular found for given classes");
//        }
//
//        return new ResponseEntity<>(classCirculars, HttpStatus.OK);
//    }

//    public ResponseEntity<Boolean> isClassLinked(String className, String session) {
//        if (studentService.isAnyStudentPresentInClass(className, session)) {
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }
//        if (adminService.isAnyAdminPresentInClass(className)) {
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }
//        if (teacherService.isAnyTeacherPresentInClass(className)) {
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(false, HttpStatus.OK);
//    }


    public ResponseEntity<String> saveRoleCirculars(List<RoleCircular> roleCirculars) {
        roleCirculars.forEach(roleCircular -> {
            Optional<RoleCircular> roleCircularOptional =
                    roleCircularRepository.findByRoleAndSession(roleCircular.getRole(), roleCircular.getSession());
            roleCircularOptional.ifPresentOrElse(existingCircular -> {
                throw new RuntimeException("Role Circular: " + roleCircular.getRole() + ":" + roleCircular.getSession() + " already Present. Aborting Save");
            }, () -> {
                roleCircular.setTimestamp(DateUtility.getCurrentTimeStamp());
                roleCircularRepository.save(roleCircular);
            });
        });
        return ResponseEntity.ok("Role Circular Data Saved");
    }

    public ResponseEntity<String> updateRoleCirculars(List<RoleCircular> roleCirculars) {
        roleCirculars.forEach(roleCircular -> {
            Optional<RoleCircular> roleCircularOptional =
                    roleCircularRepository.findByRoleAndSession(roleCircular.getRole(), roleCircular.getSession());
            roleCircularOptional.ifPresentOrElse(existingCircular -> {
                updateNonNullFields(existingCircular, roleCircular);
                existingCircular.setTimestamp(DateUtility.getCurrentTimeStamp());
                roleCircularRepository.save(existingCircular);
            }, () -> {
                throw new CircularNotFoundException("Role Circular: " + roleCircular.getRole() + ":" + roleCircular.getSession() + " not present. Aborting Update");
            });
        });
        return ResponseEntity.ok("Class Circular Data Updated");
    }

    private void updateNonNullFields(RoleCircular existingCircular, RoleCircular newCircular) {
        if (newCircular.getPaidLeaves() != null)
            existingCircular.setPaidLeaves(newCircular.getPaidLeaves());
        if (newCircular.getSickLeaves() != null)
            existingCircular.setSickLeaves(newCircular.getSickLeaves());
        if (newCircular.getCasualLeaves() != null)
            existingCircular.setCasualLeaves(newCircular.getCasualLeaves());
        if (newCircular.getBasicPayPercent() != null)
            existingCircular.setBasicPayPercent(newCircular.getBasicPayPercent());
        if (newCircular.getHraPercent() != null) existingCircular.setHraPercent(newCircular.getHraPercent());
        if (newCircular.getPfPercent() != null)
            existingCircular.setPfPercent(newCircular.getPfPercent());
        if (newCircular.getGratuityPercent() != null)
            existingCircular.setGratuityPercent(newCircular.getGratuityPercent());
        if (newCircular.getLeaveRules() != null)
            existingCircular.setLeaveRules(newCircular.getLeaveRules());
    }


//    @Transactional
//    public ResponseEntity<String> deleteClassCirculars(String className, String session) {
//        if (!roleCircularRepository.existsByClassNameAndSession(className, session)) {
//            throw new CircularNotFoundException("Class Circular Not Available for Class: " + className + " - " + session);
//        }
//        roleCircularRepository.deleteByClassNameAndSession(className, session);
//        return new ResponseEntity<>("Class Circular has been deleted for Class: " + className + " - " + session, HttpStatus.OK);
//    }
}
