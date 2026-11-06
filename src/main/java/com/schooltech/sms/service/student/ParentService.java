package com.schooltech.sms.service.student;


import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.controller.student.ParentProfileDTO;
import com.schooltech.sms.controller.student.dto.KidInfo;
import com.schooltech.sms.controller.student.dto.ParentPanelDTO;
import com.schooltech.sms.dao.client.attendence.StudentAttendenceRepository;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.dao.client.student.ParentRepository;
import com.schooltech.sms.dao.client.student.StudentOtherInfoRepository;
import com.schooltech.sms.dao.client.student.StudentRepository;
import com.schooltech.sms.dao.client.userTrash.UserTrashRepository;
import com.schooltech.sms.entity.client.payment.bank.BankDetails;
import com.schooltech.sms.entity.client.student.Parent;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.exception.ParentNotFoundException;
import com.schooltech.sms.service.communication.EmailService;
import com.schooltech.sms.service.payment.bank.BankDetailsService;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private StudentOtherInfoRepository studentOtherInfoRepository;
    @Autowired
    private BankDetailsService bankDetailsService;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private StudentAttendenceRepository studentAttendenceRepository;
    @Autowired
    private ClassCircularRepository classCircularRepository;
    @Autowired
    private AuthService authService;

    @Autowired
    private UserTrashRepository userTrashRepository;

    public ResponseEntity<List<ParentPanelDTO>> getParents() {
        List<ParentPanelDTO> parentPanelDTOS = new ArrayList<>();
        List<Parent> parentList = parentRepository.findAll();
        parentList.forEach(parent -> {
            ParentPanelDTO parentPanelDTO = new ParentPanelDTO();
            parentPanelDTO.setParent(parent);
            parentPanelDTO.setPhoneNo(authService.getUserByUsername(parent.getUsername()).getPhoneNo());

            List<Student> studentList = studentRepository.findByParentUsername(parent.getUsername());
            List<KidInfo> kidInfoList = new ArrayList<>();
            studentList.forEach(student -> {
                KidInfo kidInfo = new KidInfo();
                kidInfo.setClassName(student.getClassName());
                kidInfo.setFullName(student.getFullName());
                kidInfoList.add(kidInfo);
            });
            parentPanelDTO.setKidInfoList(kidInfoList);
            parentPanelDTOS.add(parentPanelDTO);
        });
        return new ResponseEntity<>(parentPanelDTOS, HttpStatus.OK);
    }

    public ResponseEntity<List<ParentPanelDTO>> getDeActivatedParents() {
        List<ParentPanelDTO> parentPanelDTOS = new ArrayList<>();
        List<Parent> parentList = parentRepository.getDeActivatedParents();
        parentList.forEach(parent -> {
            ParentPanelDTO parentPanelDTO = new ParentPanelDTO();
            parentPanelDTO.setParent(parent);
            parentPanelDTO.setPhoneNo(authService.getDeActivatedUser(parent.getUsername()).getPhoneNo());

//            List<Student> studentList = studentRepository.findByParentUsername(parent.getUsername());
//            List<KidInfo> kidInfoList = new ArrayList<>();
//            studentList.forEach(student -> {
//                KidInfo kidInfo = new KidInfo();
//                kidInfo.setClassName(student.getClassName());
//                kidInfo.setFullName(student.getFullName());
//                kidInfoList.add(kidInfo);
//            });
//            parentPanelDTO.setKidInfoList(kidInfoList);
            parentPanelDTOS.add(parentPanelDTO);
        });
        return new ResponseEntity<>(parentPanelDTOS, HttpStatus.OK);
    }

    public Parent getParentByUsername(String username) {
        Optional<Parent> parentOptional = parentRepository.findByUsername(username);
        return parentOptional.orElseThrow(() -> new ParentNotFoundException("Parent Not Found"));
    }

    public ParentProfileDTO getParentProfileDTOByUsername(String username) throws ParentNotFoundException {
        Optional<Parent> teacherOptional = parentRepository.findByUsername(username);
        Parent parent = teacherOptional.orElseThrow(() -> new ParentNotFoundException("Parent Not Available"));
        User user = authService.getUserByUsername((parent.getUsername()));
        BankDetails bankDetails = bankDetailsService.getBankDetails((username));
        ParentProfileDTO parentProfileDTO = new ParentProfileDTO();
        // Parent mapping
        parentProfileDTO.setUsername(parent.getUsername());
        parentProfileDTO.setParentType(parent.getParentType());
        parentProfileDTO.setFullName(parent.getFullName());
        parentProfileDTO.setGender(parent.getGender());
        parentProfileDTO.setAadharNo(parent.getAadharNo());
        parentProfileDTO.setPanNo(parent.getPanNo());
        parentProfileDTO.setDob(parent.getDob());
        parentProfileDTO.setDocuments(parent.getDocuments());
        // User mapping
        parentProfileDTO.setEmail(user.getEmail());
        parentProfileDTO.setPhoneNo(user.getPhoneNo());
        parentProfileDTO.setAltPhoneNo(user.getAltPhoneNo());
        parentProfileDTO.setUpdatedBy(user.getUpdatedBy());
        //Bank Details
        parentProfileDTO.setBankDetails(bankDetails);
        return parentProfileDTO;
    }

    public List<ParentPanelDTO> getParentByPhoneNo(String phoneNo) {
        User user = authService.getUserByPhoneNo(phoneNo);
        if (user == null) {
            throw new ParentNotFoundException("Parent not found with phone no: " + phoneNo);
        }

        Parent parent = parentRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new ParentNotFoundException("Parent not found with phone no: " + phoneNo));

        List<Student> studentList = studentRepository.findByParentUsername(parent.getUsername());
        List<KidInfo> kidInfoList = new ArrayList<>();
        studentList.forEach(student -> {
            KidInfo kidInfo = new KidInfo();
            kidInfo.setClassName(student.getClassName());
            kidInfo.setFullName(student.getFullName());
            kidInfoList.add(kidInfo);
        });

        ParentPanelDTO dto = new ParentPanelDTO();
        dto.setParent(parent);
        dto.setPhoneNo(user.getPhoneNo());
        dto.setKidInfoList(kidInfoList);
        return List.of(dto);
    }

    public List<ParentPanelDTO> getParentsByName(String name) {
        List<Parent> parentList = parentRepository.findByFullNameContainingIgnoreCase(name);
        List<ParentPanelDTO> parentPanelDTOS = new ArrayList<>();
        parentList.forEach(parent -> {
            ParentPanelDTO parentPanelDTO = new ParentPanelDTO();
            parentPanelDTO.setParent(parent);
            parentPanelDTO.setPhoneNo(authService.getUserByUsername(parent.getUsername()).getPhoneNo());
            List<Student> studentList = studentRepository.findByParentUsername(parent.getUsername());
            List<KidInfo> kidInfoList = new ArrayList<>();
            studentList.forEach(student -> {
                KidInfo kidInfo = new KidInfo();
                kidInfo.setClassName(student.getClassName());
                kidInfo.setFullName(student.getFullName());
                kidInfoList.add(kidInfo);
            });
            parentPanelDTO.setKidInfoList(kidInfoList);
            parentPanelDTOS.add(parentPanelDTO);
        });
        return parentPanelDTOS;
    }

    public List<ParentPanelDTO> getAbandonedParents() {
        List<Parent> parentList = parentRepository.findAbandonedParents();
        List<ParentPanelDTO> parentPanelDTOS = new ArrayList<>();
        parentList.forEach(parent -> {
            ParentPanelDTO parentPanelDTO = new ParentPanelDTO();
            parentPanelDTO.setParent(parent);
            parentPanelDTO.setPhoneNo(authService.getUserByUsername(parent.getUsername()).getPhoneNo());
            parentPanelDTO.setKidInfoList(new ArrayList<>()); // empty — no students
            parentPanelDTOS.add(parentPanelDTO);
        });
        return parentPanelDTOS;
    }

    public boolean isParentAadhaarExists(String aadharNo) {
        return parentRepository.existsByAadharNo(aadharNo);
    }

//    public Parent getParentByEmail(String email) {
//        Optional<Parent> parentOptional = parentRepository.findByEmail(email);
//        return parentOptional.orElseThrow(() -> new ParentNotFoundException("Parent Not Found"));
//    }


//    public Parent getParentByPhoneNo(String phoneNo) {
//        Optional<Parent> parentOptional = parentRepository.findByPhoneNo(phoneNo);
//        return parentOptional.orElseThrow(() -> new ParentNotFoundException("Parent Not Found"));
//    }

    public ResponseEntity<String> saveParents(List<Parent> parentList) {
        parentList.forEach(parent -> {
            parentRepository.findByUsername(parent.getUsername()).ifPresentOrElse(existingParent -> {
                throw new RuntimeException("Parent: " + parent.getUsername() + " already Present. Aborting Save");
            }, () -> {
                parent.setTimestamp(DateUtility.getCurrentTimeStamp());
                //User user = authService.getUserByUsername(parent.getUsername());
                //emailService.sendCredentialsEmail(user.getEmail(), parent.getUsername());
                //currently it is supprresed inside the method. hence this line acting as dummy line
            });
        });
        parentRepository.saveAll(parentList);
        return ResponseEntity.ok("Parent Data Saved");
    }

    public ResponseEntity<String> updateParents(List<Parent> parentList) {
        parentList.forEach(parent -> {
            parentRepository.findByUsername(parent.getUsername()).ifPresentOrElse(existingParent -> {
                updateNonNullFields(existingParent, parent);
                existingParent.setTimestamp(DateUtility.getCurrentTimeStamp());
                parentRepository.save(existingParent);
            }, () -> {
                throw new RuntimeException("Parent: " + parent.getUsername() + " not present. Aborting Update");
            });
        });

        return ResponseEntity.ok("Parent Data Updated");
    }

    private void updateNonNullFields(Parent existingParent, Parent newParent) {
        if (newParent.getParentType() != null) existingParent.setParentType(newParent.getParentType());
        if (newParent.getFullName() != null) existingParent.setFullName(newParent.getFullName());
        if (newParent.getAadharNo() != null) existingParent.setAadharNo(newParent.getAadharNo());
        if (newParent.getPanNo() != null) existingParent.setPanNo(newParent.getPanNo());
        if (newParent.getGender() != null) existingParent.setGender(newParent.getGender());
        if (newParent.getDob() != null) existingParent.setDob(newParent.getDob());
        if (newParent.getDocuments() != null) existingParent.setDocuments(newParent.getDocuments());
    }

//    @Transactional
//    public void updateParentPhoto(String username, String newPhotoUrl) {
//        parentRepository.findByUsername(username).ifPresent(parent -> {
//            parent.setPhotoUrl(newPhotoUrl);
//            parent.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
//            parentRepository.save(parent);
//        });
//    }

    public void deActivateParentByUsername(String username, String updatedBy) {
        // Block deletion if parent has active students
        boolean hasActiveStudents = studentRepository.existsByParentUsername(username);
        if (hasActiveStudents) {
            throw new RuntimeException("Cannot deactivate parent: active students are linked to this account.");
        }

        if (!parentRepository.existsByUsername(username)) {
            throw new RuntimeException("Parent with username " + username + " does not exist.");
        }

        parentRepository.deActivateByUsername(username);
        authService.deActivateUserByUsername(username, updatedBy);
    }

    public void activateParentWithEntity(String username, String updatedBy) {
        parentRepository.activateByUsername(username);
        authService.activateUserByUsername(username, updatedBy);
    }

    public void deleteParentByUsername(String username) {
        // Checks ALL students (active or deactivated) linked to this parent
        int studentCount = studentRepository.countStudentsByParentUsernameNative(username);
        if (studentCount > 0) {
            throw new RuntimeException("Cannot delete parent: students are still linked to this account.");
        }

        Optional<Parent> parentOptional = parentRepository.findByUsernameDeactivatedStatus(username);
        if (parentOptional.isEmpty()) {
            throw new RuntimeException("Parent with username " + username + " does not exist.");
        }

        parentRepository.deleteByUsername(username);
        userTrashRepository.deleteByUsername(username);
    }

}