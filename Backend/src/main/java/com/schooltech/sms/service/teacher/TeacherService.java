package com.schooltech.sms.service.teacher;


import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.teacher.dto.*;
import com.schooltech.sms.dao.client.attendence.TeacherAttendenceRepository;
import com.schooltech.sms.dao.client.staff.StaffRepository;
import com.schooltech.sms.dao.client.teacher.TeacherRepository;
import com.schooltech.sms.dao.client.user.UserRepository;
import com.schooltech.sms.dao.client.userTrash.UserTrashRepository;
import com.schooltech.sms.entity.client.attendence.TeacherAttendance;
import com.schooltech.sms.entity.client.payment.bank.BankDetails;
import com.schooltech.sms.entity.client.teacher.Teacher;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.exception.TeacherNotFoundException;
import com.schooltech.sms.service.communication.EmailService;
import com.schooltech.sms.service.payment.bank.BankDetailsService;
import com.schooltech.sms.utility.DateUtility;
import com.schooltech.sms.utility.UsernameUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthService authService;
    @Autowired
    private UsernameUtility usernameUtility;
    @Autowired
    private BankDetailsService bankDetailsService;
    @Autowired
    private TeacherAttendenceRepository teacherAttendenceRepository;

    @Autowired
    private UserTrashRepository userTrashRepository;

    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ResponseEntity<String> saveTeachersWithEntity(List<TeacherUserDTO> teacherUserDTOList) {
        try {
            for (TeacherUserDTO teacherUserDTO : teacherUserDTOList) {
                //saving users & teacher
                String teacherUsername = "";
                if (teacherUserDTO.getUser() != null && teacherUserDTO.getTeacher() != null) {
                    teacherUsername = usernameUtility.generateUsername(AppConstant.TEACHER_ROLE);
                    //save in user table for teacher
                    User teacherUser = teacherUserDTO.getUser();
                    teacherUser.setUsername(teacherUsername);
                    List<User> userList = new ArrayList<>();
                    userList.add(teacherUser);
                    authService.saveAllUser(userList);
                    //Save in Teacher table
                    Teacher teacher = teacherUserDTO.getTeacher();
                    teacher.setUsername(teacherUsername);
                    List<Teacher> teacherList = new ArrayList<>();
                    teacherList.add(teacher);
                    saveTeachers(teacherList);
                } else {
                    throw new RuntimeException("Creation of Teachers with other Entity Failed");
                }
            }
            //User user = authService.getUsersById(teacher.getUsername());
            //emailService.sendCredentialsEmail(createTeacherDTO.getUserList().getEmail(), createTeacherDTO.getTeacherList().getUsername());
            return ResponseEntity.ok("Teachers with Entity added successfully");
        } catch (Exception e) {
            throw new RuntimeException("Creation of Teachers with other Entity Failed");
        }
    }

    public ResponseEntity<String> saveTeachers(List<Teacher> teacherList) {
        teacherList.forEach(teacher -> {
            teacherRepository.findByUsername(teacher.getUsername()).ifPresentOrElse(existingTeacher -> {
                throw new RuntimeException("Teacher: " + teacher.getUsername() + " already Present. Aborting Save");
            }, () -> {
                teacher.setTimestamp(DateUtility.getCurrentTimeStamp());
                User user = authService.getUsersById(teacher.getUsername());
                emailService.sendCredentialsEmail(user.getEmail(), teacher.getUsername());
            });
        });
        teacherRepository.saveAll(teacherList);
        return ResponseEntity.ok("Teacher Data Saved");
    }

    public ResponseEntity<String> updateTeachers(List<Teacher> teacherList) {
        teacherList.forEach(teacher -> {
            teacherRepository.findByUsername(teacher.getUsername()).ifPresentOrElse(existingTeacher -> {
                updateNonNullFields(existingTeacher, teacher);
                existingTeacher.setTimestamp(DateUtility.getCurrentTimeStamp());
                teacherRepository.save(existingTeacher);
            }, () -> {
                throw new RuntimeException("Teacher: " + teacher.getUsername() + " not present. Aborting Update");
            });
        });
        return ResponseEntity.ok("Teacher Data Updated");
    }

//    @Transactional
//    public void updateTeacherPhoto(String username, String newPhotoUrl) {
//        teacherRepository.findByUsername(username).ifPresent(teacher -> {
//            teacher.setPhotoUrl(newPhotoUrl);
//            teacher.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
//            teacherRepository.save(teacher);
//        });
//    }

    private void updateNonNullFields(Teacher existingTeacher, Teacher newTeacher) {
        if (newTeacher.getEmployeeId() != null) existingTeacher.setEmployeeId(newTeacher.getEmployeeId());
        if (newTeacher.getFullName() != null) existingTeacher.setFullName(newTeacher.getFullName());
        if (newTeacher.getGender() != null) existingTeacher.setGender(newTeacher.getGender());
        if (newTeacher.getAadharNo() != null) existingTeacher.setAadharNo(newTeacher.getAadharNo());
        if (newTeacher.getPanNo() != null) existingTeacher.setPanNo(newTeacher.getPanNo());
        if (newTeacher.getClassNames() != null) existingTeacher.setClassNames(newTeacher.getClassNames());
        if (newTeacher.getSubjectClasses() != null) existingTeacher.setSubjectClasses(newTeacher.getSubjectClasses());
        if (newTeacher.getFatherName() != null) existingTeacher.setFatherName(newTeacher.getFatherName());
        if (newTeacher.getMotherName() != null) existingTeacher.setMotherName(newTeacher.getMotherName());
        if (newTeacher.getGuardianName() != null) existingTeacher.setGuardianName(newTeacher.getGuardianName());
        if (newTeacher.getQualification() != null) existingTeacher.setQualification(newTeacher.getQualification());
        if (newTeacher.getDesignation() != null) existingTeacher.setDesignation(newTeacher.getDesignation());
        if (newTeacher.getDob() != null) existingTeacher.setDob(newTeacher.getDob());
        if (newTeacher.getPermanentAddress() != null)
            existingTeacher.setPermanentAddress(newTeacher.getPermanentAddress());
        if (newTeacher.getCurrentAddress() != null) existingTeacher.setCurrentAddress(newTeacher.getCurrentAddress());
        if (newTeacher.getJoiningDate() != null) existingTeacher.setJoiningDate(newTeacher.getJoiningDate());
        if (newTeacher.getBusRoute() != null) existingTeacher.setBusRoute(newTeacher.getBusRoute());
        if (newTeacher.getDocuments() != null)
            existingTeacher.setDocuments(newTeacher.getDocuments());
    }

    public ResponseEntity<List<TeacherWithPhoneDTO>> getTeachers() {
        List<Teacher> teachers = teacherRepository.findAllByOrderByFullNameAsc();
        teachers.forEach(t -> t.setRole(AppConstant.TEACHER_ROLE));
        List<TeacherWithPhoneDTO> response = teachers.stream()
                .map(teacher -> {
                    User user = userRepository.findByUsername(teacher.getUsername());
                    String phoneNo = user != null ? user.getPhoneNo() : null;
                    return new TeacherWithPhoneDTO(teacher, phoneNo);
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<List<Teacher>> getDeActivatedTeachers() {
        List<Teacher> teachers = teacherRepository.getDeActivateTeachers();
        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }

    public void deActivateTeacherWithEntity(String username, String updatedBy) {
        if (!teacherRepository.existsByUsername(username)) {
            throw new RuntimeException("Teacher with username " + username + " does not exist.");
        }
        teacherRepository.deActivateByUsername(username);
        authService.deActivateUserByUsername(username, updatedBy); // pass updatedBy
    }

    public void activateTeacherWithEntity(String username, String updatedBy) {
        teacherRepository.activateByUsername(username);
        authService.activateUserByUsername(username, updatedBy);
    }

    public void deleteTeacherWithEntity(String username) {
        Optional<Teacher> teacherOptional = teacherRepository.findByUsernameDeactivatedStatus(username);
        if (teacherOptional.isEmpty()) {
            throw new RuntimeException("Teacher with username " + username + " does not exist.");
        }

        // Delete teacher from TeacherRepository
        teacherRepository.deleteByUsername(username);

        // Delete teacher from UserTrashRepository
        userTrashRepository.deleteByUsername(username);
    }

    public ResponseEntity<List<TeacherWithSalaryDTO>> getTeachersWithSalary() {
        List<TeacherWithSalaryDTO> teacherWithSalaryDTOS = new ArrayList<>();
//        List<Object[]> result = teacherRepository.findTeachersWithAttendanceStatus(date);
//        List<TeacherWithSalaryDTO> teacherWithSalaryDTOS = result.stream()
//                .map(objects -> {
//                    Teacher teacher = (Teacher) objects[0];
//                    TeacherAttendance attendance = (TeacherAttendance) objects[1];
//
//                    return getTeacherProfileDTO(teacher, attendance);
//                })
//                .toList();
        return ResponseEntity.ok(teacherWithSalaryDTOS);
    }

    public ResponseEntity<List<TeacherWithAttendanceDTO>> getTeachersWithAttendenceStatus(LocalDate date) {
        List<Object[]> result = teacherRepository.findTeachersWithAttendanceStatus(date);
        List<TeacherWithAttendanceDTO> teacherWithAttendanceDTOList = result.stream()
                .map(objects -> {
                    Teacher teacher = (Teacher) objects[0];
                    TeacherAttendance attendance = (TeacherAttendance) objects[1];

                    return getTeacherProfileDTO(teacher, attendance);
                })
                .toList();
        return ResponseEntity.ok(teacherWithAttendanceDTOList);
    }

    private TeacherWithAttendanceDTO getTeacherProfileDTO(Teacher teacher, TeacherAttendance attendance) {
        TeacherWithAttendanceDTO teacherWithAttendanceDTO = new TeacherWithAttendanceDTO();
        // ---------------- Teacher fields ----------------
        teacherWithAttendanceDTO.setUsername(teacher.getUsername());
        teacherWithAttendanceDTO.setFullName(teacher.getFullName());
        teacherWithAttendanceDTO.setEmployeeId(teacher.getEmployeeId());
        teacherWithAttendanceDTO.setDocuments(teacher.getDocuments());
        teacherWithAttendanceDTO.setGender(teacher.getGender());
        teacherWithAttendanceDTO.setClassNames(teacher.getClassNames());
        teacherWithAttendanceDTO.setRole(AppConstant.TEACHER_ROLE);

        // ---------------- Attendance fields ----------------
        if (attendance != null) {
            teacherWithAttendanceDTO.setStatus(attendance.getStatus() != null ? attendance.getStatus().toString() : null);
            teacherWithAttendanceDTO.setApproved(attendance.getApproved() != null ? attendance.getApproved().toString() : null);
            teacherWithAttendanceDTO.setCheckInTime(attendance.getCheckInTime() != null ? attendance.getCheckInTime().toString() : null);
            teacherWithAttendanceDTO.setCheckOutTime(attendance.getCheckOutTime() != null ? attendance.getCheckOutTime().toString() : null);
        }
        return teacherWithAttendanceDTO;
    }


    public TeacherProfileDTO getTeacherProfileDTOByUsername(String teacherId) throws TeacherNotFoundException {
        Optional<Teacher> teacherOptional = teacherRepository.findByUsername(teacherId);
        Teacher teacher = teacherOptional.orElseThrow(() -> new TeacherNotFoundException("Teacher Not Available"));
        User user = authService.getUserByUsername(teacher.getUsername());
        BankDetails bankDetails = bankDetailsService.getBankDetails(teacherId);
        TeacherProfileDTO teacherProfileDTO = new TeacherProfileDTO();
        // Teacher mapping
        teacherProfileDTO.setUsername(teacher.getUsername());
        teacherProfileDTO.setEmployeeId(teacher.getEmployeeId());
        teacherProfileDTO.setFullName(teacher.getFullName());
        teacherProfileDTO.setGender(teacher.getGender());
        teacherProfileDTO.setAadharNo(teacher.getAadharNo());
        teacherProfileDTO.setPanNo(teacher.getPanNo());
        teacherProfileDTO.setClassNames(teacher.getClassNames());
        teacherProfileDTO.setSubjectClasses(teacher.getSubjectClasses());
        teacherProfileDTO.setFatherName(teacher.getFatherName());
        teacherProfileDTO.setMotherName(teacher.getMotherName());
        teacherProfileDTO.setGuardianName(teacher.getGuardianName());
        teacherProfileDTO.setQualification(teacher.getQualification());
        teacherProfileDTO.setDesignation(teacher.getDesignation());
        teacherProfileDTO.setDob(teacher.getDob());
        teacherProfileDTO.setJoiningDate(teacher.getJoiningDate());
        teacherProfileDTO.setPermanentAddress(teacher.getPermanentAddress());
        teacherProfileDTO.setCurrentAddress(teacher.getCurrentAddress());
        teacherProfileDTO.setBusRoute(teacher.getBusRoute());
        teacherProfileDTO.setDocuments(teacher.getDocuments());
        //teacherProfileDTO.setUpdatedBy(teacher.getUpdatedBy());
        teacherProfileDTO.setRole(AppConstant.TEACHER_ROLE);
        // User mapping
        teacherProfileDTO.setEmail(user.getEmail());
        teacherProfileDTO.setPhoneNo(user.getPhoneNo());
        teacherProfileDTO.setAltPhoneNo(user.getAltPhoneNo());
        teacherProfileDTO.setUpdatedBy(user.getUpdatedBy());
        //Bank Details
        teacherProfileDTO.setBankDetails(bankDetails);
        return teacherProfileDTO;
    }

    public Teacher getTeacherByUsername(String teacherId) throws TeacherNotFoundException {
        Optional<Teacher> teacherOptional = teacherRepository.findByUsername(teacherId);
        return teacherOptional.orElseThrow(() -> new TeacherNotFoundException("Teacher Not Available"));
    }

    public boolean isEmployeeAadhaarExists(String aadharNo) {
        // check teacher table first
        if (teacherRepository.existsByAadharNo(aadharNo)) {
            return true;
        }
        // then check staff table
        return staffRepository.existsByAadharNo(aadharNo);
    }

    public boolean isEmployeeIdExist(String employeeId) {
        // check teacher table first
        if (teacherRepository.existsByEmployeeIdIgnoreCase(employeeId)) {
            return true;
        }
        // then check staff table
        return staffRepository.existsByEmployeeIdIgnoreCase(employeeId);
    }


//    public Teacher getTeacherByEmailId(String teacherId) throws TeacherNotFoundException {
//        Optional<Teacher> teacher = teacherRepository.findByEmail(teacherId);
//        return teacher.orElseThrow(() -> new TeacherNotFoundException("Teacher Not Available"));
//    }

    //    public Teacher getTeacherByPhoneNo(String phoneNo) throws TeacherNotFoundException {
//        Optional<Teacher> teacher = teacherRepository.findByPhoneNo(phoneNo);
//        return teacher.orElseThrow(() -> new TeacherNotFoundException("Teacher Not Available"));
//    }
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
}
