package com.schooltech.sms.service;


import com.schooltech.sms.dao.client.staff.StaffRepository;
import com.schooltech.sms.dao.client.student.ParentRepository;
import com.schooltech.sms.dao.client.teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GeneralService {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ParentRepository parentRepository;

    public boolean isAnyTeacherPresentInClass(String className) {
        return teacherRepository.findAll()
                .stream()
                .anyMatch(teacher ->
                        teacher.getClassNames() != null &&
                                teacher.getClassNames()
                                        .stream()
                                        .anyMatch(c -> c.equalsIgnoreCase(className))
                );
    }

    public ResponseEntity<Boolean> isEmployeeIdExists(String employeeId) {
        boolean isPresent = (staffRepository.existsByEmployeeIdIgnoreCaseNative(employeeId) > 0
                || teacherRepository.existsByEmployeeIdIgnoreCaseNative(employeeId) > 0);
        if (isPresent) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
    }

    public boolean isPanNoAlreadyExists(String panNo) {
        return teacherRepository.existsByPanNo(panNo)
                || parentRepository.existsByPanNo(panNo)
                || staffRepository.existsByPanNo(panNo);
    }
}
