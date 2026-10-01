//package com.schooltech.sms.service.admin;
//
//
//import com.schooltech.security.jwt.service.AuthService;
//import com.schooltech.sms.constant.AppConstant;
//import com.schooltech.sms.controller.admin.dto.AdminPanelDTO;
//import com.schooltech.sms.controller.admin.dto.AdminProfileDTO;
//import com.schooltech.sms.controller.admin.dto.AdminUserDTO;
//import com.schooltech.sms.dao.client.admin.AdminRepository;
//import com.schooltech.sms.entity.client.admin.Admin;
//import com.schooltech.sms.entity.client.payment.bank.BankDetails;
//import com.schooltech.sms.entity.client.user.User;
//import com.schooltech.sms.exception.AdminNotFoundException;
//import com.schooltech.sms.service.communication.EmailService;
//import com.schooltech.sms.service.payment.bank.BankDetailsService;
//import com.schooltech.sms.utility.DateUtility;
//import com.schooltech.sms.utility.UsernameUtility;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@Service
//public class AdminService {
//    @Autowired
//    private AdminRepository adminRepository;
//    @Autowired
//    private EmailService emailService;
//    @Autowired
//    private AuthService authService;
//    @Autowired
//    private UsernameUtility usernameUtility;
//    @Autowired
//    private BankDetailsService bankDetailsService;
//
//    public ResponseEntity<List<AdminPanelDTO>> getAdmins() {
//        List<AdminPanelDTO> adminPanelDTOList = new ArrayList<>();
//        List<Admin> admins = adminRepository.findAllAdmins();
//        admins.forEach(admin -> {
//            AdminPanelDTO adminPanelDTO = new AdminPanelDTO();
//            User user = authService.getUserByUsername(admin.getUsername());
//            adminPanelDTO.setUsername(admin.getUsername());
//            adminPanelDTO.setFullName(admin.getFullName());
//            adminPanelDTO.setEmployeeId(admin.getEmployeeId());
//            adminPanelDTO.setDesignation(admin.getDesignation());
//            adminPanelDTO.setDocuments(admin.getDocuments());
//            adminPanelDTO.setPhoneNo(user.getPhoneNo());
//            adminPanelDTOList.add(adminPanelDTO);
//        });
//        return new ResponseEntity<>(adminPanelDTOList, HttpStatus.OK);
//    }
//
//    public ResponseEntity<List<AdminPanelDTO>> getDeActivatedAdmins() {
//        List<AdminPanelDTO> adminPanelDTOList = new ArrayList<>();
//        List<Admin> admins = adminRepository.getDeActivatedAdmins();
//        admins.forEach(admin -> {
//            AdminPanelDTO adminPanelDTO = new AdminPanelDTO();
//            //User user = authService.getUserByUsername(admin.getUsername());
//            adminPanelDTO.setUsername(admin.getUsername());
//            adminPanelDTO.setFullName(admin.getFullName());
//            adminPanelDTO.setEmployeeId(admin.getEmployeeId());
//            adminPanelDTO.setDesignation(admin.getDesignation());
//            adminPanelDTO.setDocuments(admin.getDocuments());
//            //adminPanelDTO.setPhoneNo(user.getPhoneNo());
//            adminPanelDTOList.add(adminPanelDTO);
//        });
//        return new ResponseEntity<>(adminPanelDTOList, HttpStatus.OK);
//    }
//
//    public ResponseEntity<List<AdminPanelDTO>> getSuperAdmins() {
//        List<AdminPanelDTO> adminPanelDTOList = new ArrayList<>();
//        List<Admin> admins = adminRepository.findAllSuperAdminsExcludingDefault();
//        admins.forEach(admin -> {
//            AdminPanelDTO adminPanelDTO = new AdminPanelDTO();
//            User user = authService.getUserByUsername(admin.getUsername());
//            adminPanelDTO.setUsername(admin.getUsername());
//            adminPanelDTO.setFullName(admin.getFullName());
//            adminPanelDTO.setEmployeeId(admin.getEmployeeId());
//            adminPanelDTO.setDesignation(admin.getDesignation());
//            adminPanelDTO.setDocuments(admin.getDocuments());
//            adminPanelDTO.setPhoneNo(user.getPhoneNo());
//            adminPanelDTOList.add(adminPanelDTO);
//        });
//        return new ResponseEntity<>(adminPanelDTOList, HttpStatus.OK);
//    }
//
//
//    public ResponseEntity<List<AdminPanelDTO>> getDeActivatedSuperAdmins() {
//        List<AdminPanelDTO> adminPanelDTOList = new ArrayList<>();
//        List<Admin> admins = adminRepository.findAllDeActivatedSuperAdminsExcludingDefault();
//        admins.forEach(admin -> {
//            AdminPanelDTO adminPanelDTO = new AdminPanelDTO();
//            //User user = authService.getUserByUsername(admin.getUsername());
//            adminPanelDTO.setUsername(admin.getUsername());
//            adminPanelDTO.setFullName(admin.getFullName());
//            adminPanelDTO.setEmployeeId(admin.getEmployeeId());
//            adminPanelDTO.setDesignation(admin.getDesignation());
//            adminPanelDTO.setDocuments(admin.getDocuments());
//            //adminPanelDTO.setPhoneNo(user.getPhoneNo());
//            adminPanelDTOList.add(adminPanelDTO);
//        });
//        return new ResponseEntity<>(adminPanelDTOList, HttpStatus.OK);
//    }
//
//    public void deActivateAdminWithEntity(String username) {
//        if (!adminRepository.existsByUsername(username)) {
//            throw new RuntimeException("Admin with username " + username + " does not exist.");
//        }
//        adminRepository.deActivateByUsername(username);
//        authService.deActivateUserByUsername(username);
//    }
//
//    public void activateAdminWithEntity(String username) {
//        adminRepository.activateByUsername(username);
//        authService.activateUserByUsername(username);
//    }
//
//    public ResponseEntity<AdminProfileDTO> getAdminProfileDTOByUsername(String adminId) {
//        Optional<Admin> adminOptional = adminRepository.findByUsername(adminId);
//        Admin admin = adminOptional.orElseThrow(() -> new AdminNotFoundException("Admin Not Available"));
//        User user = authService.getUserByUsername(adminId);
//        BankDetails bankDetails = bankDetailsService.getBankDetails(adminId);
//        AdminProfileDTO adminProfileDTO = new AdminProfileDTO();
//        // Teacher mapping
//        adminProfileDTO.setUsername(admin.getUsername());
//        adminProfileDTO.setEmployeeId(admin.getEmployeeId());
//        adminProfileDTO.setFullName(admin.getFullName());
//        adminProfileDTO.setGender(admin.getGender());
//        adminProfileDTO.setAadharNo(admin.getAadharNo());
//        adminProfileDTO.setPanNo(admin.getPanNo());
//        adminProfileDTO.setClassNames(admin.getClassNames());
//        adminProfileDTO.setFatherName(admin.getFatherName());
//        adminProfileDTO.setMotherName(admin.getMotherName());
//        adminProfileDTO.setGuardianName(admin.getGuardianName());
//        adminProfileDTO.setQualification(admin.getQualification());
//        adminProfileDTO.setDesignation(admin.getDesignation());
//        adminProfileDTO.setDob(admin.getDob());
//        adminProfileDTO.setJoiningDate(admin.getJoiningDate());
//        adminProfileDTO.setPermanentAddress(admin.getPermanentAddress());
//        adminProfileDTO.setCurrentAddress(admin.getCurrentAddress());
//        adminProfileDTO.setBusRoute(admin.getBusRoute());
//        adminProfileDTO.setDocuments(admin.getDocuments());
//        // User mapping
//        adminProfileDTO.setEmail(user.getEmail());
//        adminProfileDTO.setPhoneNo(user.getPhoneNo());
//        adminProfileDTO.setAltPhoneNo(user.getAltPhoneNo());
//        //
//        adminProfileDTO.setBankDetails(bankDetails);
//
//        return new ResponseEntity<>(adminProfileDTO, HttpStatus.OK);
//    }
//
//
//    public Admin getAdminByUsername(String adminId) {
//        Optional<Admin> adminOptional = adminRepository.findByUsername(adminId);
//        return adminOptional.orElseThrow(() -> new AdminNotFoundException("Admin Not Available"));
//
//    }
//
//    @Transactional
//    public ResponseEntity<String> saveAdminsWithEntity(AdminUserDTO adminUserDTO) {
//        try {
//            //saving users & teacher
//            String adminUsername = "";
//            if (adminUserDTO.getUser() != null && adminUserDTO.getAdmin() != null) {
//                adminUsername = usernameUtility.generateUsername(AppConstant.ADMIN_ROLE);
//                //save in user table for teacher
//                User teacherUser = adminUserDTO.getUser();
//                teacherUser.setUsername(adminUsername);
//                List<User> userList = new ArrayList<>();
//                userList.add(teacherUser);
//                authService.saveAllUser(userList);
//                //Save in Teacher table
//                Admin admin = adminUserDTO.getAdmin();
//                admin.setUsername(adminUsername);
//                Map<String, String> adminDocuments = new HashMap<>();
//                admin.setDocuments(adminDocuments);
//                List<Admin> adminList = new ArrayList<>();
//                adminList.add(admin);
//                saveAdmins(adminList);
//            } else {
//                throw new RuntimeException("Creation of Admin with other Entity Failed");
//            }
//            //emailService.sendCredentialsEmail(admin.getEmail(), admin.getUsername());
//            return ResponseEntity.ok("Admin with Entity added successfully");
//        } catch (Exception e) {
//            throw new RuntimeException("Creation of Admin with other Entity Failed");
//        }
//    }
//
//    @Transactional
//    public ResponseEntity<String> saveSuperAdminsWithEntity(AdminUserDTO adminUserDTO) {
//        try {
//            //saving users & teacher
//            String adminUsername = "";
//            if (adminUserDTO.getUser() != null && adminUserDTO.getAdmin() != null) {
//                adminUsername = usernameUtility.generateUsername(AppConstant.SUPER_ADMIN_ROLE);
//                //save in user table for teacher
//                User teacherUser = adminUserDTO.getUser();
//                teacherUser.setUsername(adminUsername);
//                List<User> userList = new ArrayList<>();
//                userList.add(teacherUser);
//                authService.saveAllUser(userList);
//                //Save in Teacher table
//                Admin admin = adminUserDTO.getAdmin();
//                admin.setUsername(adminUsername);
//                List<Admin> adminList = new ArrayList<>();
//                adminList.add(admin);
//                saveAdmins(adminList);
//            } else {
//                throw new RuntimeException("Creation of Super Admin with other Entity Failed");
//            }
//            //emailService.sendCredentialsEmail(admin.getEmail(), admin.getUsername());
//            return ResponseEntity.ok("SuperAdmin with Entity added successfully");
//        } catch (Exception e) {
//            throw new RuntimeException("Creation of SuperAdmin with other Entity Failed");
//        }
//    }
//
//
//    public ResponseEntity<String> saveAdmins(List<Admin> adminData) {
//        adminData.forEach(admin -> {
//            adminRepository.findByUsername(admin.getUsername()).ifPresentOrElse(existingAdmin -> {
//                throw new RuntimeException("Admin: " + admin.getUsername() + " already Present. Aborting Save");
//            }, () -> {
//                admin.setTimestamp(DateUtility.getCurrentTimeStamp());
//                User user = authService.getUserByUsername(admin.getUsername());
//                emailService.sendCredentialsEmail(user.getEmail(), admin.getUsername());
//            });
//        });
//        adminRepository.saveAll(adminData);
//        return ResponseEntity.ok("Admin Data Saved");
//    }
//
//    public ResponseEntity<String> updateAdmins(List<Admin> adminData) {
//        adminData.forEach(admin -> {
//            adminRepository.findByUsername(admin.getUsername()).ifPresentOrElse(existingAdmin -> {
//                updateNonNullFields(existingAdmin, admin);
//                existingAdmin.setTimestamp(DateUtility.getCurrentTimeStamp());
//                adminRepository.save(existingAdmin);
//            }, () -> {
//                throw new RuntimeException("Admin: " + admin.getUsername() + " not present. Aborting Update");
//            });
//        });
//
//        return ResponseEntity.ok("Admin Data Updated");
//    }
//
//
//    private void updateNonNullFields(Admin existingAdmin, Admin newAdmin) {
//        if (newAdmin.getEmployeeId() != null) existingAdmin.setEmployeeId(newAdmin.getEmployeeId());
//        if (newAdmin.getFullName() != null) existingAdmin.setFullName(newAdmin.getFullName());
//        if (newAdmin.getGender() != null) existingAdmin.setGender(newAdmin.getGender());
//        if (newAdmin.getAadharNo() != null) existingAdmin.setAadharNo(newAdmin.getAadharNo());
//        if (newAdmin.getPanNo() != null) existingAdmin.setPanNo(newAdmin.getPanNo());
//        if (newAdmin.getClassNames() != null) existingAdmin.setClassNames(newAdmin.getClassNames());
//        if (newAdmin.getFatherName() != null) existingAdmin.setFatherName(newAdmin.getFatherName());
//        if (newAdmin.getMotherName() != null) existingAdmin.setMotherName(newAdmin.getMotherName());
//        if (newAdmin.getGuardianName() != null) existingAdmin.setGuardianName(newAdmin.getGuardianName());
//        if (newAdmin.getQualification() != null) existingAdmin.setQualification(newAdmin.getQualification());
//        if (newAdmin.getDesignation() != null) existingAdmin.setDesignation(newAdmin.getDesignation());
//        if (newAdmin.getDob() != null) existingAdmin.setDob(newAdmin.getDob());
//        if (newAdmin.getPermanentAddress() != null) existingAdmin.setPermanentAddress(newAdmin.getPermanentAddress());
//        if (newAdmin.getCurrentAddress() != null) existingAdmin.setCurrentAddress(newAdmin.getCurrentAddress());
//        if (newAdmin.getJoiningDate() != null) existingAdmin.setJoiningDate(newAdmin.getJoiningDate());
//        if (newAdmin.getBusRoute() != null) existingAdmin.setBusRoute(newAdmin.getBusRoute());
//        if (newAdmin.getDocuments() != null) existingAdmin.setDocuments(newAdmin.getDocuments());
//    }
//
////    @Transactional
////    public void updateAdminPhoto(String username, String newPhotoUrl) {
////        adminRepository.findByUsername(username).ifPresent(admin -> {
////            admin.setPhotoUrl(newPhotoUrl);
////            admin.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
////            adminRepository.save(admin);
////        });
////    }
//
//    public boolean isAnyAdminPresentInClass(String className) {
//        return adminRepository.findAll()
//                .stream()
//                .anyMatch(admin ->
//                        admin.getClassNames() != null &&
//                                admin.getClassNames()
//                                        .stream()
//                                        .anyMatch(c -> c.equalsIgnoreCase(className))
//                );
//    }
//
//}