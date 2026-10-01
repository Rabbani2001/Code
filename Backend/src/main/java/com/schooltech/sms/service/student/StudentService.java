package com.schooltech.sms.service.student;


import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.student.dto.*;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.dao.client.payment.StudentBusFeeRepository;
import com.schooltech.sms.dao.client.payment.StudentFeeRepository;
import com.schooltech.sms.dao.client.student.ParentRepository;
import com.schooltech.sms.dao.client.student.StudentOtherInfoRepository;
import com.schooltech.sms.dao.client.student.StudentRepository;
import com.schooltech.sms.dao.client.user.UserRepository;
import com.schooltech.sms.dao.client.userTrash.UserTrashRepository;
import com.schooltech.sms.entity.client.attendence.StudentAttendance;
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.circulars.ClassQuality;
import com.schooltech.sms.entity.client.payment.StudentBusFee;
import com.schooltech.sms.entity.client.payment.StudentFee;
import com.schooltech.sms.entity.client.student.*;
import com.schooltech.sms.entity.client.student.dto.StudentProfileDTO;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.exception.dto.StudentNotFoundException;
import com.schooltech.sms.service.communication.EmailService;
import com.schooltech.sms.utility.DateUtility;
import com.schooltech.sms.utility.UsernameUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentFeeRepository studentFeeRepository;
    @Autowired
    private StudentBusFeeRepository studentBusFeeRepository;
    @Autowired//keep this service as it will be used later when studnet is saved in the system currently it is disabled
    private EmailService emailService;
    @Autowired
    private StudentOtherInfoRepository studentOtherInfoRepository;
    @Autowired
    private ClassCircularRepository classCircularRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UsernameUtility usernameUtility;
    @Autowired
    private ParentService parentService;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTrashRepository userTrashRepository;

    ///////////////////// STUDENT ///////////////////////////////
    public ResponseEntity<List<Student>> getStudents() {
        List<Student> students = studentRepository.findAllByOrderByFullNameAsc();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    public ResponseEntity<List<Student>> getDeActivatedStudents() {
        List<Student> students = studentRepository.getDeActivatedStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    public void deActivateStudentByUsername(String username, String parentUsername, String updatedBy) {
        if (!studentRepository.existsByUsername(username)) {
            throw new RuntimeException("Student with username " + username + " does not exist.");
        }

        // Clear rollNo FIRST
        studentRepository.clearRollNoByUsername(username);

        // Update updatedBy + timestamp BEFORE deactivating,
        // so the @Where(is_active=true) filter doesn't hide the row
        studentRepository.findByUsername(username).ifPresent(student -> {
            student.setUpdatedBy(updatedBy);
            student.setTimestamp(DateUtility.getCurrentTimeStamp());
            studentRepository.save(student);
        });

        // Deactivate LAST
        studentRepository.deActivateByUsername(username);

        // Check if the parent has other active students (siblings)
        boolean hasActiveSiblings = studentRepository.existsByParentUsername(parentUsername);
        if (!hasActiveSiblings) {
            parentRepository.deActivateByUsername(parentUsername);
            authService.deActivateUserByUsername(parentUsername, updatedBy);
        }
    }

    public void activateStudentWithEntity(String username, String parentUsername, String updatedBy) {
        studentRepository.activateByUsername(username);


        // save updatedBy
        if (updatedBy != null) {
            studentRepository.findByUsername(username).ifPresent(student -> {
                student.setUpdatedBy(updatedBy);
                student.setTimestamp(DateUtility.getCurrentTimeStamp());
                studentRepository.save(student);
            });
        }

        Optional<Parent> deactivatedParent =
                parentRepository.findByUsernameDeactivatedStatus(parentUsername);

        if (deactivatedParent.isPresent()) {
            parentRepository.activateByUsername(parentUsername);
            authService.activateUserByUsername(parentUsername, updatedBy);
        }
    }

    public void deleteStudentByUsername(String username, String parentUsername) {
        // Check if the deactivated student exists
        Optional<Student> studentOptional = studentRepository.findByUsernameDeactivatedStatus(username);

        if (studentOptional.isEmpty()) {
            throw new RuntimeException("Deleted student with username " + username + " does not exist.");
        }

        // Delete the student from the repository
        studentRepository.deleteByUsername(username);

        // Check if the parent has other active students (siblings)
        boolean hasActiveSiblings = studentRepository.existsByParentUsername(parentUsername);
        if (!hasActiveSiblings) {
            // Delete the parent from the parent repository
            parentRepository.deleteByUsername(parentUsername);

            // Delete the parent from the userTrash repository
            userTrashRepository.deleteByUsername(parentUsername);
        }
    }

    public StudentProfileDTO getStudentProfileDTOByUsername(String username) {
        Optional<Student> studentOptional = studentRepository.findStudentByUsername(username);
        Student student = studentOptional.orElseThrow(() -> new StudentNotFoundException("Student is Not Available"));
        User user = authService.getUserByUsername(student.getParentUsername());

        StudentProfileDTO dto = new StudentProfileDTO();
        // ---------------- Student basic info ----------------
        dto.setUsername(student.getUsername());
        dto.setParentUsername(student.getParentUsername());
        dto.setSession(student.getSession());
        dto.setParentType(student.getParentType());
        dto.setFullName(student.getFullName());
        dto.setAdmissionNo(student.getAdmissionNo());
        dto.setAdmissionDate(student.getAdmissionDate());
        dto.setAdmissionStatus(student.getAdmissionStatus());
        dto.setAadharNo(student.getAadharNo());
        dto.setApaarNo(student.getApaarNo());
        dto.setGender(student.getGender());
        dto.setBloodGroup(student.getBloodGroup());
        dto.setClassName(student.getClassName());
        dto.setRollNo(student.getRollNo());
        dto.setFatherName(student.getFatherName());
        dto.setMotherName(student.getMotherName());
        dto.setGuardianName(student.getGuardianName());
        dto.setDob(student.getDob());
        dto.setMedium(student.getMedium());
        dto.setPermanentAddress(student.getPermanentAddress());
        dto.setCurrentAddress(student.getCurrentAddress());
        dto.setRuralOrUrban(student.getRuralOrUrban());
        dto.setBusRoute(student.getBusRoute());
        dto.setDocuments(student.getDocuments());
        dto.setStudentUpdatedBy(student.getUpdatedBy());
        dto.setParentActions(student.getParentActions());
        // ---------------- User (Auth table) ----------------
        if (user != null) {
            dto.setEmail(user.getEmail());
            dto.setPhoneNo(user.getPhoneNo());
            dto.setAltPhoneNo(user.getAltPhoneNo());
            dto.setUpdatedBy(user.getUpdatedBy());
        }
        return dto;
    }

    public ResponseEntity<List<StudentWithOtherInfoDTO>> getStudentsWithOtherInfoByClass(String className, String session) {

        List<Student> studentsDB = studentRepository
                .findByClassNameAndSessionOrderByFullNameAsc(className, session);

        if (studentsDB.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }

        // Collect all usernames
        List<String> usernames = studentsDB.stream()
                .map(Student::getUsername)
                .collect(Collectors.toList());

        // Single query for all StudentOtherInfo
        Map<String, StudentOtherInfo> otherInfoMap = studentOtherInfoRepository
                .findByUsernameIn(usernames)
                .stream()
                .collect(Collectors.toMap(StudentOtherInfo::getUsername, info -> info));

        List<StudentWithOtherInfoDTO> result = studentsDB.stream().map(student -> {
            StudentWithOtherInfoDTO dto = new StudentWithOtherInfoDTO();
            StudentOtherInfo info = otherInfoMap.getOrDefault(student.getUsername(), new StudentOtherInfo());
            User user = authService.getUserByUsername(student.getParentUsername());

            // Student fields
            dto.setUsername(student.getUsername());
            dto.setFullName(student.getFullName());
            dto.setSession(student.getSession());
            dto.setGender(student.getGender());
            dto.setBloodGroup(student.getBloodGroup());
            dto.setApaarNo(student.getApaarNo());
            dto.setParentType(student.getParentType());
            dto.setFatherName(student.getFatherName());
            dto.setMotherName(student.getMotherName());
            dto.setGuardianName(student.getGuardianName());
            dto.setAdmissionStatus(student.getAdmissionStatus());
            dto.setAdmissionNo(student.getAdmissionNo());
            dto.setAdmissionDate(student.getAdmissionDate());
            dto.setClassName(student.getClassName());
            dto.setRollNo(student.getRollNo());
            dto.setDob(student.getDob());
            dto.setMedium(student.getMedium());
            dto.setAadharNo(student.getAadharNo());
            dto.setPermanentAddress(student.getPermanentAddress());
            dto.setCurrentAddress(student.getCurrentAddress());
            dto.setRuralOrUrban(student.getRuralOrUrban());
            dto.setBusRoute(student.getBusRoute());

            // User fields
            if (user != null) {
                dto.setEmail(user.getEmail());
                dto.setPhoneNo(user.getPhoneNo());
                dto.setAltPhoneNo(user.getAltPhoneNo());
            }

            // StudentOtherInfo fields
            dto.setFamilyId(info.getFamilyId());
            dto.setFatherDob(info.getFatherDob());
            dto.setFatherAadharNo(info.getFatherAadharNo());
            dto.setFatherOccupation(info.getFatherOccupation());
            dto.setFatherEducation(info.getFatherEducation());
            dto.setMotherDob(info.getMotherDob());
            dto.setMotherAadharNo(info.getMotherAadharNo());
            dto.setMotherOccupation(info.getMotherOccupation());
            dto.setMotherEducation(info.getMotherEducation());
            dto.setGuardianDob(info.getGuardianDob());
            dto.setGuardianAadharNo(info.getGuardianAadharNo());
            dto.setGuardianOccupation(info.getGuardianOccupation());
            dto.setGuardianEducation(info.getGuardianEducation());
            dto.setGuardianGender(info.getGuardianGender());
            dto.setReligion(info.getReligion());
            dto.setCaste(info.getCaste());
            dto.setNationality(info.getNationality());
            dto.setDomicile(info.getDomicile());
            dto.setIsMinority(info.getIsMinority());
            dto.setMinorityCategory(info.getMinorityCategory());
            dto.setIsBPL(info.getIsBPL());
            dto.setStream(info.getStream());
            dto.setSubjectsOpted(info.getSubjectsOpted());
            dto.setSubjectOptional(info.getSubjectOptional());
            dto.setPreviousSchoolName(info.getPreviousSchoolName());
            dto.setPreviousAdmissionNo(info.getPreviousAdmissionNo());
            dto.setPreviousSchoolCode(info.getPreviousSchoolCode());
            dto.setPreviousLeavingDate(info.getPreviousLeavingDate());
            dto.setPreviousClassPassed(info.getPreviousClassPassed());
            dto.setPreviousMarksObtained(info.getPreviousMarksObtained());

            return dto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public Student getStudentUsername(String username) {
        Optional<Student> studentOptional = studentRepository.findByUsername(username);
        return studentOptional.orElseThrow(() -> new StudentNotFoundException("Student is Not Available"));
    }

    public Student getStudentByAdmissionNo(String admissionNo) {
        Optional<Student> student = studentRepository.findByAdmissionNo(admissionNo);
        return student.orElseThrow(() -> new StudentNotFoundException("Student Not Found"));
    }

    public Student getStudentByAadharNo(String aadharNo) {
        Optional<Student> student = studentRepository.findByAadharNo(aadharNo);
        return student.orElseThrow(() -> new StudentNotFoundException("Student Not Found"));
    }

    public boolean isStudentAadhaarExists(String aadharNo) {
        return studentRepository.existsByAadharNo(aadharNo);
    }

    public Student getStudentByApaarNo(String apaarNo) {
        Optional<Student> student = studentRepository.findByApaarNo(apaarNo);
        return student.orElseThrow(() -> new StudentNotFoundException("Student Not Found"));
    }

    public ResponseEntity<List<StudentPanelDTO>> getStudentsByClass(String className, String session) {
        List<Student> studentsDB = studentRepository.findByClassNameAndSessionOrderByFullNameAsc(className, session);
        List<StudentPanelDTO> studentPanelDTOList = new ArrayList<>();
        studentsDB.forEach(student -> {
            User user = authService.getUserByUsername(student.getParentUsername());
            StudentPanelDTO studentPanelDTO = new StudentPanelDTO();
            studentPanelDTO.setUsername(student.getUsername());
            studentPanelDTO.setParentUsername(student.getParentUsername());
            studentPanelDTO.setSession(student.getSession());
            studentPanelDTO.setParentType(student.getParentType());
            studentPanelDTO.setFullName(student.getFullName());
            studentPanelDTO.setAdmissionNo(student.getAdmissionNo());
            studentPanelDTO.setAdmissionStatus(student.getAdmissionStatus());
            studentPanelDTO.setGender(student.getGender());
            studentPanelDTO.setClassName(student.getClassName());
            studentPanelDTO.setRollNo(student.getRollNo());
            studentPanelDTO.setDob(student.getDob());
            studentPanelDTO.setAadharNo(student.getAadharNo());
            studentPanelDTO.setPermanentAddress(student.getPermanentAddress());
            studentPanelDTO.setCurrentAddress(student.getCurrentAddress());
            studentPanelDTO.setBusRoute(student.getBusRoute());
            studentPanelDTO.setBloodGroup(student.getBloodGroup());
            studentPanelDTO.setFatherName(student.getFatherName());
            studentPanelDTO.setMotherName(student.getMotherName());
            studentPanelDTO.setGuardianName(student.getGuardianName());
            studentPanelDTO.setDocuments(student.getDocuments());

            StudentOtherInfo studentOtherInfo = studentOtherInfoRepository.findByUsername(student.getUsername())
                    .orElse(new StudentOtherInfo()); // Handle case where StudentOtherInfo might not exist
            studentPanelDTO.setProfileCompleted(
                    isStudentProfileCompleted(student, studentOtherInfo)
            );
            if (user != null) {
                studentPanelDTO.setEmail(user.getEmail());
                studentPanelDTO.setPhoneNo(user.getPhoneNo());
            }
            studentPanelDTOList.add(studentPanelDTO);
        });
        return new ResponseEntity<>(studentPanelDTOList, HttpStatus.OK);
    }

    private boolean isStudentProfileCompleted(Student student, StudentOtherInfo studentOtherInfo) {

        return isNotBlank(student.getAadharNo())
                && student.getDob() != null
                && isNotBlank(student.getGender())
                && isNotBlank(student.getFatherName())
                && isNotBlank(student.getMotherName())
                && isNotBlank(student.getPermanentAddress())
                && isNotBlank(student.getCurrentAddress())
                && isNotBlank(studentOtherInfo.getReligion())
                && isNotBlank(student.getAdmissionNo());
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public ResponseEntity<List<Student>> getStudentsByBusRoute(String busRoute) {
        List<Student> students = studentRepository.findByBusRouteOrderByFullNameAsc(busRoute);
        // List<StudentPanelDTO> studentPanelDTOList = new ArrayList<>();
//        students.forEach(student -> {
//            User user = authService.getUserByUsername(student.getParentUsername());
//            StudentPanelDTO studentPanelDTO = new StudentPanelDTO();
//            studentPanelDTO.setUsername(student.getUsername());
//            studentPanelDTO.setParentUsername(student.getParentUsername());
//            studentPanelDTO.setParentType(student.getParentType());
//            studentPanelDTO.setFullName(student.getFullName());
//            studentPanelDTO.setAdmissionNo(student.getAdmissionNo());
//            studentPanelDTO.setGender(student.getGender());
//            studentPanelDTO.setClassName(student.getClassName());
//            studentPanelDTO.setRollNo(student.getRollNo());
//            studentPanelDTO.setFatherName(student.getFatherName());
//            studentPanelDTO.setMotherName(student.getMotherName());
//            studentPanelDTO.setGuardianName(student.getGuardianName());
//            studentPanelDTO.setPhotoUrl(student.getPhotoUrl());
//            if (user != null) {
//                studentPanelDTO.setEmail(user.getEmail());
//                studentPanelDTO.setPhoneNo(user.getPhoneNo());
//            }
//            studentPanelDTOList.add(studentPanelDTO);
//        });
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    public ResponseEntity<List<Student>> getStudentsByFatherName(String fatherName) {
        List<Student> studentList = studentRepository.findStudentsByFatherNameStartingWith(fatherName);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    public ResponseEntity<List<Student>> getStudentsByStudentUserId(String username) {
        List<Student> studentList = studentRepository.findStudentsByStudentIdStartingWith(username);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    public ResponseEntity<List<Student>> getStudentsByStudentName(String studentName) {
        List<Student> studentList = studentRepository.findByFullNameContainingIgnoreCase(studentName);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }


//    public ResponseEntity<List<Student>> getStudentsByPhoneNo(String phoneNo) {
//        Optional<User> user = userRepository.findByUsernameOrEmailOrPhoneNo(phoneNo);
//        if (user.isEmpty())
//            throw new RuntimeException("Phone No Not found");
//        Optional<List<Student>> studentList = studentRepository.findByPhoneNo(phoneNo);
//        List<Student> list = studentList.orElseThrow(() -> new StudentNotFoundException("Student Not Found"));
//        return new ResponseEntity<>(list, HttpStatus.OK);
//    }

    public ResponseEntity<AddStudentOrSiblingDTO> getStudentsByPhoneNo(String phoneNo) {
        List<Object[]> results = studentRepository.findStudentsByPhoneNo(phoneNo);
        AddStudentOrSiblingDTO dto = new AddStudentOrSiblingDTO();
        List<StudentSiblingDTO> studentSiblingDTOS = new ArrayList<>();
        User user = null;
        Parent parent = null;
        for (Object[] row : results) {
            user = (User) row[0];
            parent = (Parent) row[1];

            Student student = (Student) row[2];
            if (student != null) {
                StudentSiblingDTO studentSiblingDTO = new StudentSiblingDTO();
                studentSiblingDTO.setFullName(student.getFullName());
                studentSiblingDTO.setFatherName(student.getFatherName());
                studentSiblingDTO.setUsername(student.getUsername());
                studentSiblingDTO.setParentUsername(student.getParentUsername());
                studentSiblingDTO.setClassName(student.getClassName());
                studentSiblingDTO.setAdmissionNo(student.getAdmissionNo());
                studentSiblingDTO.setAdmissionStatus(student.getAdmissionStatus().toString());
                studentSiblingDTO.setRollNo(student.getRollNo());
                studentSiblingDTO.setGender(student.getGender());
                studentSiblingDTO.setDocuments(student.getDocuments());

                StudentOtherInfo studentOtherInfo = studentOtherInfoRepository.findByUsername(student.getUsername())
                        .orElse(new StudentOtherInfo()); // Handle case where StudentOtherInfo might not exist
                studentSiblingDTO.setProfileCompleted(
                        isStudentProfileCompleted(student, studentOtherInfo)
                );
                if (user != null) {
                    studentSiblingDTO.setPhoneNo(user.getPhoneNo());
                }
                studentSiblingDTOS.add(studentSiblingDTO);
            }
        }
        if (parent != null) {
            dto.setParentUsername(parent.getUsername());
            dto.setParentName(parent.getFullName());
            dto.setParentType(parent.getParentType());
        }
        if (user != null) {
            dto.setPhoneNo(user.getPhoneNo());
            dto.setEmail(user.getEmail());
        }
        dto.setSiblingList(studentSiblingDTOS);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    public ResponseEntity<List<StudentWithAttendanceDTO>> getStudentsByClassWithAttendence(String className, LocalDate date) {
        List<Object[]> result = findStudentWithAttendenceStatus(className, date);
        List<StudentWithAttendanceDTO> studentWithAttendanceDTOList = result.stream()
                .map(objects -> {
                    Student student = (Student) objects[0];
                    StudentAttendance attendance = (StudentAttendance) objects[1];
                    StudentWithAttendanceDTO studentWithAttendanceDTO = new StudentWithAttendanceDTO();
                    // ---------------- Student fields ----------------
                    studentWithAttendanceDTO.setUsername(student.getUsername());
                    studentWithAttendanceDTO.setParentUsername(student.getParentUsername());
                    studentWithAttendanceDTO.setFatherName(student.getFatherName());
                    studentWithAttendanceDTO.setClassName(student.getClassName());
                    studentWithAttendanceDTO.setRollNo(student.getRollNo());
                    studentWithAttendanceDTO.setFullName(student.getFullName());
                    studentWithAttendanceDTO.setGender(student.getGender());
                    // ---------------- Attendance fields ----------------
                    if (attendance != null) {
                        studentWithAttendanceDTO.setStatus(attendance.getStatus() != null ? attendance.getStatus().toString() : null);
                        studentWithAttendanceDTO.setApproved(attendance.getApproved() != null ? attendance.getApproved().toString() : null);
                        studentWithAttendanceDTO.setCheckInTime(attendance.getCheckInTime() != null ? attendance.getCheckInTime().toString() : null);
                        studentWithAttendanceDTO.setCheckOutTime(attendance.getCheckOutTime() != null ? attendance.getCheckOutTime().toString() : null);
                    }
                    return studentWithAttendanceDTO;
                })
                .toList();
        return ResponseEntity.ok(studentWithAttendanceDTOList);
    }

    private List<Object[]> findStudentWithAttendenceStatus(String className, LocalDate date) {
        String currentSession = DateUtility.getAcademicSessionByDate(date);
        if (className.equalsIgnoreCase("all")) {
            List<Object[]> allClassStudentsWIthAttendance = new ArrayList<>();
            //List<ClassCircular> classCirculars = classCircularRepository.findAll();
            List<ClassCircular> classCirculars = classCircularRepository.findBySession(currentSession);
            classCirculars.stream().forEach(cc -> {
                allClassStudentsWIthAttendance.addAll(studentRepository.findStudentWithAttendenceStatus(cc.getClassName(), date, currentSession));
            });
            return allClassStudentsWIthAttendance;
        } else {
            return studentRepository.findStudentWithAttendenceStatus(className, date, currentSession);
        }
    }

    //Below 3 methods fetching students with their school fee details
    public List<StudentWithFeeDetailsDTO> getStudentsByClassWithFeeDetails(String className, String session) {
        List<Object[]> results = studentRepository.findStudentsWithFeeDetails(className, session);

        List<StudentWithFeeDetailsDTO> response = new ArrayList<>();
        for (Object[] row : results) {
            User user = (User) row[0];
            Student student = (Student) row[1];
            StudentFee studentFee = (StudentFee) row[2];
            //code that check is there any studentFee pending for previous Session
            if (studentFee == null) {
                StudentFee studentFeePreviousSession = studentFeeRepository.findByStudentIdAndSession(student.getUsername(), DateUtility.getPreviousSession(session));
                if (studentFeePreviousSession != null) {
                    if (studentFeePreviousSession.getOutstanding() != null && (studentFeePreviousSession.getOutstanding() != 0L)) {
                        studentFee = studentFeePreviousSession;
                    }
                }
            }
            StudentWithFeeDetailsDTO dto = new StudentWithFeeDetailsDTO(student, studentFee, user.getEmail(), user.getPhoneNo());
            response.add(dto);
        }
        return response;
    }

    public List<StudentWithFeeDetailsDTO> getAllStudentsFeeOnlyDetails(String className, String session) {
        List<Object[]> results = studentRepository.findStudentsWithFeeDetails(className, session);

        List<StudentWithFeeDetailsDTO> response = new ArrayList<>();
        for (Object[] row : results) {
            //User user = (User) row[0];
            Student student = (Student) row[1];
            StudentFee studentFee = (StudentFee) row[2];
            //code that check is there any studentFee pending for previous Session
            if (studentFee == null) {
                StudentFee studentFeePreviousSession = studentFeeRepository.findByStudentIdAndSession(student.getUsername(), DateUtility.getPreviousSession(session));
                if (studentFeePreviousSession != null) {
                    if (studentFeePreviousSession.getOutstanding() != null && (studentFeePreviousSession.getOutstanding() != 0L)) {
                        studentFee = studentFeePreviousSession;
                    }
                }
            }
            StudentWithFeeDetailsDTO dto = new StudentWithFeeDetailsDTO(student, studentFee, null, null);
            response.add(dto);
        }
        return response;
    }

    public List<StudentWithFeeDetailsDTO> getStudentsByPhoneNoWithFeeDetails(String phoneNo, String session) {
        List<Object[]> results = studentRepository.findStudentsByPhoneNoWithFeeDetails(phoneNo, session);

        List<StudentWithFeeDetailsDTO> response = new ArrayList<>();
        for (Object[] row : results) {
            User user = (User) row[0];
            Student student = (Student) row[1];
            StudentFee studentFee = (StudentFee) row[2];
            //code that check is there any studentFee pending for previous Session
            if (studentFee == null) {
                StudentFee studentFeePreviousSession = studentFeeRepository.findByStudentIdAndSession(student.getUsername(), DateUtility.getPreviousSession(session));
                if (studentFeePreviousSession != null) {
                    if (studentFeePreviousSession.getOutstanding() != null && studentFeePreviousSession.getOutstanding() != 0L) {
                        studentFee = studentFeePreviousSession;
                    }
                }
            }
            StudentWithFeeDetailsDTO dto = new StudentWithFeeDetailsDTO(student, studentFee, user.getEmail(), user.getPhoneNo());
            response.add(dto);
        }
        return response;
    }

    public List<StudentWithFeeDetailsDTO> getStudentsByAdmissionNoWithFeeDetails(String admissionNo, String session) {
        List<Object[]> results = studentRepository.findStudentsByAdmissionNoWithFeeDetails(admissionNo, session);

        List<StudentWithFeeDetailsDTO> response = new ArrayList<>();
        for (Object[] row : results) {
            User user = (User) row[0];
            Student student = (Student) row[1];
            StudentFee studentFee = (StudentFee) row[2];
            //code that check is there any studentFee pending for previous Session
            if (studentFee == null) {
                StudentFee studentFeePreviousSession = studentFeeRepository.findByStudentIdAndSession(student.getUsername(), DateUtility.getPreviousSession(session));
                if (studentFeePreviousSession != null) {
                    if (studentFeePreviousSession.getOutstanding() != null && studentFeePreviousSession.getOutstanding() != 0L) {
                        studentFee = studentFeePreviousSession;
                    }
                }
            }
            StudentWithFeeDetailsDTO dto = new StudentWithFeeDetailsDTO(student, studentFee, user.getEmail(), user.getPhoneNo());
            response.add(dto);
        }
        return response;
    }

    //Below 3 methods fetching students with their bus fee details
    public List<StudentWithBusFeeDetailsDTO> getStudentsByClassWithBusFeeDetails(String className, String session) {
        List<Object[]> results = studentRepository.findStudentsWithBusFeeDetails(className, session);

        List<StudentWithBusFeeDetailsDTO> response = new ArrayList<>();
        for (Object[] row : results) {
            User user = (User) row[0];
            Student student = (Student) row[1];
            StudentBusFee studentBusFee = (StudentBusFee) row[2];
            //code that check is there any studentFee pending for previous Session
            if (studentBusFee == null) {
                StudentBusFee studentBusFeePreviousSession = studentBusFeeRepository.findByStudentIdAndSession(student.getUsername(), DateUtility.getPreviousSession(session));
                if (studentBusFeePreviousSession != null) {
                    if (studentBusFeePreviousSession.getOutstanding() != null && studentBusFeePreviousSession.getOutstanding() != 0L) {
                        studentBusFee = studentBusFeePreviousSession;
                    }
                }
            }
            StudentWithBusFeeDetailsDTO dto = new StudentWithBusFeeDetailsDTO(student, studentBusFee, user.getEmail(), user.getPhoneNo());
            response.add(dto);
        }
        return response;
    }

    public List<StudentWithBusFeeDetailsDTO> getAllStudentsBusFeeOnlyDetails(String className, String session) {
        List<Object[]> results = studentRepository.findStudentsWithBusFeeDetails(className, session);

        List<StudentWithBusFeeDetailsDTO> response = new ArrayList<>();
        for (Object[] row : results) {
            Student student = (Student) row[1];
            StudentBusFee studentBusFee = (StudentBusFee) row[2];
            // check for outstanding bus fee from previous session
            if (studentBusFee == null) {
                StudentBusFee studentBusFeePreviousSession =
                        studentBusFeeRepository.findByStudentIdAndSession(student.getUsername(), DateUtility.getPreviousSession(session));
                if (studentBusFeePreviousSession != null) {
                    if (studentBusFeePreviousSession.getOutstanding() != null && (studentBusFeePreviousSession.getOutstanding() != 0L)) {
                        studentBusFee = studentBusFeePreviousSession;
                    }
                }
            }
            StudentWithBusFeeDetailsDTO dto = new StudentWithBusFeeDetailsDTO(student, studentBusFee, null, null);
            response.add(dto);
        }
        return response;
    }

    public List<StudentWithBusFeeDetailsDTO> getStudentsByPhoneNoWithBusFeeDetails(String phoneNo, String session) {
        List<Object[]> results = studentRepository.findStudentsByPhoneNoWithBusFeeDetails(phoneNo, session);

        List<StudentWithBusFeeDetailsDTO> response = new ArrayList<>();
        for (Object[] row : results) {
            User user = (User) row[0];
            Student student = (Student) row[1];
            StudentBusFee studentBusFee = (StudentBusFee) row[2];
            //code that check is there any studentFee pending for previous Session
            if (studentBusFee == null) {
                StudentBusFee studentBusFeePreviousSession = studentBusFeeRepository.findByStudentIdAndSession(student.getUsername(), DateUtility.getPreviousSession(session));
                if (studentBusFeePreviousSession != null) {
                    if (studentBusFeePreviousSession.getOutstanding() != null && studentBusFeePreviousSession.getOutstanding() != 0L) {
                        studentBusFee = studentBusFeePreviousSession;
                    }
                }
            }
            StudentWithBusFeeDetailsDTO dto = new StudentWithBusFeeDetailsDTO(student, studentBusFee, user.getEmail(), user.getPhoneNo());
            response.add(dto);
        }
        return response;
    }

    public List<StudentWithBusFeeDetailsDTO> getStudentsByAdmissionNoWithBusFeeDetails(String admissionNo, String session) {
        List<Object[]> results = studentRepository.findStudentsByAdmissionNoWithBusFeeDetails(admissionNo, session);

        List<StudentWithBusFeeDetailsDTO> response = new ArrayList<>();
        for (Object[] row : results) {
            User user = (User) row[0];
            Student student = (Student) row[1];
            StudentBusFee studentBusFee = (StudentBusFee) row[2];
            //code that check is there any studentFee pending for previous Session
            if (studentBusFee == null) {
                StudentBusFee studentBusFeePreviousSession = studentBusFeeRepository.findByStudentIdAndSession(student.getUsername(), DateUtility.getPreviousSession(session));
                if (studentBusFeePreviousSession != null) {
                    if (studentBusFeePreviousSession.getOutstanding() != null && studentBusFeePreviousSession.getOutstanding() != 0L) {
                        studentBusFee = studentBusFeePreviousSession;
                    }
                }
            }
            StudentWithBusFeeDetailsDTO dto = new StudentWithBusFeeDetailsDTO(student, studentBusFee, user.getEmail(), user.getPhoneNo());
            response.add(dto);
        }
        return response;
    }

    public boolean getStudentFeeStatus(String username, String session) {
        StudentFee studentFee = studentFeeRepository.findByStudentIdAndSession(username, session);

        if (studentFee == null) {
            return false; // not found in fee table
        }

        return studentFee.getOutstanding() != null && studentFee.getOutstanding() > 0;
    }


    public List<StudentWithExamScheduleDTO> getStudentsWithExamSchedule(String className, String session) {
        List<Object[]> results = studentRepository.findStudentsWithExamSchedule(className, session);

        List<StudentWithExamScheduleDTO> response = new ArrayList<>();
        for (Object[] row : results) {
            Student student = (Student) row[0];
            StudentExams studentExams = (StudentExams) row[1];

            Map<String, List<StudentExamSchedule>> examSchedules = studentExams != null
                    ? studentExams.getExamSchedules()
                    : Map.of();

            Map<String, List<StudentExamScheduleGrade>> examSchedulesGrade = studentExams != null
                    ? studentExams.getExamSchedulesGrade()
                    : Map.of();

            Map<String, List<StudentExamSchedule>> testSchedules = studentExams != null
                    ? studentExams.getTestSchedules()
                    : Map.of();

            Map<String, List<StudentExamScheduleGrade>> testSchedulesGrade = studentExams != null
                    ? studentExams.getTestSchedulesGrade()
                    : Map.of();

            Map<String, String> examScheduleComments = studentExams != null
                    ? studentExams.getExamScheduleComments()
                    : Map.of();

            Map<String, String> testScheduleComments = studentExams != null
                    ? studentExams.getTestScheduleComments()
                    : Map.of();

            Map<String, List<ClassQuality>> classQualities = studentExams != null
                    ? studentExams.getClassQualities()
                    : Map.of();

            String examRemarks = studentExams != null ? studentExams.getExamRemarks() : null;

            String resultId = studentExams != null ? studentExams.getResultId() : null;

            StudentWithExamScheduleDTO dto = new StudentWithExamScheduleDTO(student, examSchedules,examSchedulesGrade, testSchedules,testSchedulesGrade, examScheduleComments, testScheduleComments, examRemarks, resultId, session, classQualities);
            response.add(dto);
        }
        return response;
    }

    public ResponseEntity<List<Student>> getStudentsByParent(String username) {
        List<Student> studentList = studentRepository.findByParentUsername(username);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    public ResponseEntity<String> saveStudentWithAssociatedEntity(List<StudentParentUserDTO> studentParentUserDTOList) {
        try {
            for (StudentParentUserDTO studentParentUserDTO : studentParentUserDTOList) {
                //saving users & parents
                String parentUsername = "";
                if ((studentParentUserDTO.getUser() != null && studentParentUserDTO.getUser().getUsername() == null)
                        || (studentParentUserDTO.getParent() != null && studentParentUserDTO.getParent().getUsername() == null)) {

                    parentUsername = usernameUtility.generateUsername(AppConstant.PARENT_ROLE);
                    //save in user table for parent
                    User parentUser = studentParentUserDTO.getUser();
                    parentUser.setUsername(parentUsername);
                    List<User> userList = new ArrayList<>();
                    userList.add(parentUser);
                    authService.saveAllUser(userList);
                    //Save in Parent table
                    Parent parent = studentParentUserDTO.getParent();
                    parent.setUsername(parentUsername);
                    List<Parent> parentList = new ArrayList<>();
                    parentList.add(parent);
                    parentService.saveParents(parentList);

                }
                //saving students & studentOtherInfos
                if (studentParentUserDTO.getStudentList() != null && !studentParentUserDTO.getStudentList().isEmpty()) {
                    for (int i = 0; i < studentParentUserDTO.getStudentList().size(); i++) {
                        String studentUsername = usernameUtility.generateUsername(AppConstant.STUDENT_ROLE); //need to replace it with db logic for concurency

                        Student student = studentParentUserDTO.getStudentList().get(i);
                        student.setUsername(studentUsername);

                        StudentOtherInfo studentOtherInfo = studentParentUserDTO.getStudentOtherInfoList().get(i);
                        studentOtherInfo.setUsername(studentUsername);

                        if (student.getParentUsername() == null)
                            student.setParentUsername(parentUsername);
                    }
                    //save in student table
                    saveStudents(studentParentUserDTO.getStudentList());
                    //save in studentotherinfo table
                    saveStudentOtherInfo(studentParentUserDTO.getStudentOtherInfoList());
                }
            }
            return ResponseEntity.ok("Student Data Saved With Other Entity");
        } catch (Exception e) {
            throw new RuntimeException("Creation of Student with other Entity Failed");
        }
    }

    public ResponseEntity<String> saveStudents(List<Student> students) {
//        try {
        students.forEach(student -> {
            studentRepository.findByUsername(student.getUsername()).ifPresentOrElse(existingStudent -> {
                throw new RuntimeException("Student: " + student.getUsername() + " already Present. Aborting Save");
            }, () -> {
                student.setTimestamp(DateUtility.getCurrentTimeStamp());
                //emailService.sendCredentialsEmail(student.getEmail(), student.getUsername());
            });
        });
        studentRepository.saveAll(students);
        return ResponseEntity.ok("Student Data Saved");
//        } catch (DataIntegrityViolationException e) {
//            //System.out.println("Constraint violation: " + e.getMessage());
//            throw new RuntimeException(e.getMessage());
//        }

    }

    public ResponseEntity<String> updateStudents(List<Student> students) {
//        try {
        students.forEach(student -> {
            studentRepository.findByUsername(student.getUsername()).ifPresentOrElse(existingStudent -> {
                updateNonNullFields(existingStudent, student);
                existingStudent.setTimestamp(DateUtility.getCurrentTimeStamp());
                studentRepository.save(existingStudent);
            }, () -> {
                throw new RuntimeException("Student: " + student.getUsername() + " not present. Aborting Update");
            });
        });

        return ResponseEntity.ok("Student Data Updated");
//        } catch (DataIntegrityViolationException e) {
//            //System.out.println("Constraint violation: " + e.getMessage());
//            throw new RuntimeException(e.getMessage());
//        }
    }

    private void updateNonNullFields(Student existingStudent, Student newStudent) {
        if (newStudent.getParentUsername() != null) existingStudent.setParentUsername(newStudent.getParentUsername());
        if (newStudent.getSession() != null) existingStudent.setSession(newStudent.getSession());
        if (newStudent.getParentType() != null) existingStudent.setParentType(newStudent.getParentType());
        if (newStudent.getFullName() != null) existingStudent.setFullName(newStudent.getFullName());
        if (newStudent.getAdmissionNo() != null) existingStudent.setAdmissionNo(newStudent.getAdmissionNo());
        if (newStudent.getAdmissionDate() != null) existingStudent.setAdmissionDate(newStudent.getAdmissionDate());
        if (newStudent.getAdmissionStatus() != null)
            existingStudent.setAdmissionStatus(newStudent.getAdmissionStatus());
        if (newStudent.getAadharNo() != null) existingStudent.setAadharNo(newStudent.getAadharNo());
        if (newStudent.getApaarNo() != null) existingStudent.setApaarNo(newStudent.getApaarNo());
        if (newStudent.getGender() != null) existingStudent.setGender(newStudent.getGender());
        if (newStudent.getBloodGroup() != null) existingStudent.setBloodGroup(newStudent.getBloodGroup());
        if (newStudent.getClassName() != null) existingStudent.setClassName(newStudent.getClassName());
        if (newStudent.getRollNo() != null) existingStudent.setRollNo(newStudent.getRollNo());
        if (newStudent.getFatherName() != null) existingStudent.setFatherName(newStudent.getFatherName());
        if (newStudent.getMotherName() != null) existingStudent.setMotherName(newStudent.getMotherName());
        if (newStudent.getGuardianName() != null) existingStudent.setGuardianName(newStudent.getGuardianName());
        if (newStudent.getDob() != null) existingStudent.setDob(newStudent.getDob());
        if (newStudent.getMedium() != null) existingStudent.setMedium(newStudent.getMedium());
        if (newStudent.getPermanentAddress() != null)
            existingStudent.setPermanentAddress(newStudent.getPermanentAddress());
        if (newStudent.getCurrentAddress() != null) existingStudent.setCurrentAddress(newStudent.getCurrentAddress());
        if (newStudent.getRuralOrUrban() != null) existingStudent.setRuralOrUrban(newStudent.getRuralOrUrban());
        if (newStudent.getBusRoute() != null) existingStudent.setBusRoute(newStudent.getBusRoute());
        if (newStudent.getDocuments() != null)
            existingStudent.setDocuments(newStudent.getDocuments());
        if (newStudent.getUpdatedBy() != null) existingStudent.setUpdatedBy(newStudent.getUpdatedBy());
        if (newStudent.getParentActions() != null) existingStudent.setParentActions(newStudent.getParentActions());
    }

    public ResponseEntity<String> updateStudentsByAadharNo(List<Student> students) {
        students.stream().forEach(student -> {
            Optional<Student> studentDB = studentRepository.findByAadharNo(student.getAadharNo());
            if (studentDB.get() != null) {
                student.setId(studentDB.get().getId());
            }
            student.setTimestamp(DateUtility.getCurrentTimeStamp());
        });
        studentRepository.saveAll(students);
        return ResponseEntity.ok("Student Data Updated Successfully By aadhar no");
    }

    public ResponseEntity<String> promoteStudents(List<Student> students) {
        students.forEach(student -> {
            studentRepository.findByUsername(student.getUsername()).ifPresentOrElse(existingStudent -> {
                if (student.getClassName() != null) existingStudent.setClassName(student.getClassName());
                if (student.getSession() != null) existingStudent.setSession(student.getSession());

                existingStudent.setRollNo(null); // always reset on promotion — new class, new roll numbers

                existingStudent.setTimestamp(DateUtility.getCurrentTimeStamp());
                studentRepository.save(existingStudent);
            }, () -> {
                throw new RuntimeException("Student: " + student.getUsername() + " not present. Aborting Promotion");
            });
        });

        return ResponseEntity.ok("Students Promoted");
    }
//    @Transactional
//    public void updateStudentPhotoUrl(String username, String newPhotoUrl) {
//        studentRepository.findByUsername(username).ifPresent(student -> {
//            student.setPhotoUrl(newPhotoUrl);
//            student.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
//            studentRepository.save(student);
//        });
//    }


    ///////////////////// Student OtherInfo ////////////////////////////

    public ResponseEntity<StudentOtherInfo> getStudentsInfo(String username) {
        Optional<StudentOtherInfo> studentOtherInfoOptional = studentOtherInfoRepository.findStudentOtherInfoByUsername(username);
        StudentOtherInfo studentOtherInfo = studentOtherInfoOptional.orElseThrow(() -> new StudentNotFoundException("Student Info NOt found"));
        return new ResponseEntity<>(studentOtherInfo, HttpStatus.OK);
    }

    public ResponseEntity<String> saveStudentOtherInfo(List<StudentOtherInfo> studentOtherInfos) {
        studentOtherInfos.forEach(studentOtherInfo -> {
            Optional<StudentOtherInfo> existingInfoOptional = studentOtherInfoRepository.findByUsername(studentOtherInfo.getUsername());
            existingInfoOptional.ifPresentOrElse(existingInfo -> {
                throw new RuntimeException("Student OtherInfo: " + studentOtherInfo.getUsername() + " already Present. Aborting Save");
            }, () -> {
                studentOtherInfo.setTimestamp(DateUtility.getCurrentTimeStamp());
            });
        });
        studentOtherInfoRepository.saveAll(studentOtherInfos);
        return ResponseEntity.ok("Student Other Info Data Saved");
    }

    public ResponseEntity<String> updateStudentOtherInfo(List<StudentOtherInfo> studentOtherInfos, String updatedBy) {
        studentOtherInfos.forEach(studentOtherInfo -> {
            Optional<StudentOtherInfo> existingInfoOptional = studentOtherInfoRepository.findByUsername(studentOtherInfo.getUsername());

            existingInfoOptional.ifPresentOrElse(existingInfo -> {
                updateStudnetOtherInfoNonNullFields(existingInfo, studentOtherInfo);
                existingInfo.setTimestamp(DateUtility.getCurrentTimeStamp());
                studentOtherInfoRepository.save(existingInfo);

                // save updated by in Student table if StudentOtherInfo changes.
                studentRepository.findByUsername(studentOtherInfo.getUsername())
                        .ifPresent(student -> {
                            student.setUpdatedBy(updatedBy);
                            studentRepository.save(student);
                        });
            }, () -> {
                throw new RuntimeException("Student OtherInfo: " + studentOtherInfo.getUsername() + " not present. Aborting Update");
            });
        });

        return ResponseEntity.ok("Student Other Info Data Saved");
    }

    private void updateStudnetOtherInfoNonNullFields(StudentOtherInfo existingInfo, StudentOtherInfo newInfo) {
        if (newInfo.getFamilyId() != null) existingInfo.setFamilyId(newInfo.getFamilyId());

        if (newInfo.getFatherDob() != null) existingInfo.setFatherDob(newInfo.getFatherDob());
        if (newInfo.getFatherAadharNo() != null) existingInfo.setFatherAadharNo(newInfo.getFatherAadharNo());
        if (newInfo.getFatherOccupation() != null) existingInfo.setFatherOccupation(newInfo.getFatherOccupation());
        if (newInfo.getFatherEducation() != null) existingInfo.setFatherEducation(newInfo.getFatherEducation());

        if (newInfo.getMotherDob() != null) existingInfo.setMotherDob(newInfo.getMotherDob());
        if (newInfo.getMotherAadharNo() != null) existingInfo.setMotherAadharNo(newInfo.getMotherAadharNo());
        if (newInfo.getMotherOccupation() != null) existingInfo.setMotherOccupation(newInfo.getMotherOccupation());
        if (newInfo.getMotherEducation() != null) existingInfo.setMotherEducation(newInfo.getMotherEducation());

        if (newInfo.getGuardianDob() != null) existingInfo.setGuardianDob(newInfo.getGuardianDob());
        if (newInfo.getGuardianAadharNo() != null) existingInfo.setGuardianAadharNo(newInfo.getGuardianAadharNo());
        if (newInfo.getGuardianOccupation() != null)
            existingInfo.setGuardianOccupation(newInfo.getGuardianOccupation());
        if (newInfo.getGuardianEducation() != null) existingInfo.setGuardianEducation(newInfo.getGuardianEducation());
        if (newInfo.getGuardianGender() != null) existingInfo.setGuardianGender(newInfo.getGuardianGender());

        if (newInfo.getReligion() != null) existingInfo.setReligion(newInfo.getReligion());
        if (newInfo.getCaste() != null) existingInfo.setCaste(newInfo.getCaste());
        if (newInfo.getNationality() != null) existingInfo.setNationality(newInfo.getNationality());
        if (newInfo.getDomicile() != null) existingInfo.setDomicile(newInfo.getDomicile());
        if (newInfo.getIsMinority() != null) existingInfo.setIsMinority(newInfo.getIsMinority());
        if (newInfo.getMinorityCategory() != null) existingInfo.setMinorityCategory(newInfo.getMinorityCategory());
        if (newInfo.getIsBPL() != null) existingInfo.setIsBPL(newInfo.getIsBPL());
        if (newInfo.getStream() != null) existingInfo.setStream(newInfo.getStream());
        if (newInfo.getSubjectsOpted() != null) existingInfo.setSubjectsOpted(newInfo.getSubjectsOpted());
        if (newInfo.getSubjectOptional() != null) existingInfo.setSubjectOptional(newInfo.getSubjectOptional());
        if (newInfo.getPreviousSchoolName() != null)
            existingInfo.setPreviousSchoolName(newInfo.getPreviousSchoolName());
        if (newInfo.getPreviousAdmissionNo() != null)
            existingInfo.setPreviousAdmissionNo(newInfo.getPreviousAdmissionNo());
        if (newInfo.getPreviousSchoolCode() != null)
            existingInfo.setPreviousSchoolCode(newInfo.getPreviousSchoolCode());
        if (newInfo.getPreviousLeavingDate() != null)
            existingInfo.setPreviousLeavingDate(newInfo.getPreviousLeavingDate());
        if (newInfo.getPreviousClassPassed() != null)
            existingInfo.setPreviousClassPassed(newInfo.getPreviousClassPassed());
        if (newInfo.getPreviousMarksObtained() != null)
            existingInfo.setPreviousMarksObtained(newInfo.getPreviousMarksObtained());

    }

    public boolean isAnyStudentPresentInClass(String className, String session) {
        return studentRepository.existsByClassNameAndSession(className, session);
    }


}