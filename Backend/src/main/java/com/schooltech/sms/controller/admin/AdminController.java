//package com.schooltech.sms.controller.admin;
//
//
//import com.schooltech.sms.controller.admin.dto.AdminPanelDTO;
//import com.schooltech.sms.controller.admin.dto.AdminProfileDTO;
//import com.schooltech.sms.controller.admin.dto.AdminUserDTO;
//import com.schooltech.sms.entity.client.admin.Admin;
//import com.schooltech.sms.service.admin.AdminService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//public class AdminController {
//    @Autowired
//    private AdminService adminService;
//
//    @GetMapping("/getAdmins")
//    public ResponseEntity<List<AdminPanelDTO>> getAdmins() {
//        return adminService.getAdmins();
//    }
//
//    @GetMapping("/getDeActivatedAdmins")
//    public ResponseEntity<List<AdminPanelDTO>> getDeActivatedAdmins() {
//        return adminService.getDeActivatedAdmins();
//    }
//
//    @GetMapping("/getSuperAdmins")
//    public ResponseEntity<List<AdminPanelDTO>> getSuperAdmins() {
//        return adminService.getSuperAdmins();
//    }
//
//    @GetMapping("/getDeActivatedSuperAdmins")
//    public ResponseEntity<List<AdminPanelDTO>> getDeActivatedSuperAdmins() {
//        return adminService.getDeActivatedSuperAdmins();
//    }
//
//    @DeleteMapping("/deActivateAdmin/{username}")
//    @Transactional
//    public ResponseEntity<String> deActivateAdminByUsername(@PathVariable String username) {
//        adminService.deActivateAdminWithEntity(username);
//        return ResponseEntity.ok("Admin with username " + username + " has been deactivated successfully.");
//    }
//
//    @PutMapping("/activateAdmin/{username}")
//    @Transactional
//    public ResponseEntity<String> activateAdminByUsername(@PathVariable String username) {
//        adminService.activateAdminWithEntity(username);
//        return ResponseEntity.ok("Admin with username " + username + " has been activated successfully.");
//    }
//
//
//    @GetMapping("/getAdminByUsername/{username}")
//    public ResponseEntity<AdminProfileDTO> getAdminByUsername(@PathVariable String username) {
//        return adminService.getAdminProfileDTOByUsername(username);
//    }
//
////    @GetMapping("/getAdminByEmail/{email}")
////    public ResponseEntity<Admin> getAdminByEmail(@PathVariable String email) {
////        return adminService.getAdminByEmail(email);
////    }
////
////    @GetMapping("/getAdminByPhoneNo/{phoneNo}")
////    public ResponseEntity<Admin> getAdminByPhoneNo(@PathVariable String phoneNo) {
////        return adminService.getAdminByPhoneNo(phoneNo);
////    }
//
//    @PostMapping("/saveAdminWithEntity")
//    public ResponseEntity<String> saveAdminsWithEntity(@RequestBody AdminUserDTO adminUserDTO) {
//        return adminService.saveAdminsWithEntity(adminUserDTO);
//    }
//
//    @PostMapping("/saveSuperAdminWithEntity")
//    public ResponseEntity<String> saveSuperAdminsWithEntity(@RequestBody AdminUserDTO adminUserDTO) {
//        return adminService.saveSuperAdminsWithEntity(adminUserDTO);
//    }
//
//    @PostMapping("/saveAdmins")
//    public ResponseEntity<String> saveAdmins(@RequestBody List<Admin> adminData) {
//        return adminService.saveAdmins(adminData);
//    }
//
//    @PutMapping("/updateAdmins")
//    public ResponseEntity<String> updateAdmins(@RequestBody List<Admin> adminData) {
//        return adminService.updateAdmins(adminData);
//    }
//
//}
