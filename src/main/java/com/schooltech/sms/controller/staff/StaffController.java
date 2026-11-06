package com.schooltech.sms.controller.staff;

import com.schooltech.sms.controller.staff.dto.*;
import com.schooltech.sms.entity.client.staff.Staff;
import com.schooltech.sms.service.staff.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class StaffController {
    @Autowired
    private StaffService staffService;

    @GetMapping("/getAllStaffs")
    public ResponseEntity<List<StaffPanelDTO>> getAllStaffs() {
        return staffService.getAllStaffs();
    }

    @GetMapping("/getStaffs")
    public ResponseEntity<List<StaffPanelDTO>> getStaffs(@RequestParam String role) {
        return staffService.getStaffs(role);
    }

    @DeleteMapping("/deActivateStaff/{username}")
    @Transactional
    public ResponseEntity<String> deActivateStaffByUsername(
            @PathVariable String username,
            @RequestBody StaffDeactivationRequestDTO request) {

        staffService.deActivateStaffWithEntity(username, request.getUpdatedBy());
        return ResponseEntity.ok("Staff with username " + username + " has been deactivated successfully.");
    }

    @PutMapping("/activateStaff/{username}")
    @Transactional
    public ResponseEntity<String> activateStaffByUsername(
            @PathVariable String username,
            @RequestBody StaffDeactivationRequestDTO request) {

        staffService.activateStaffWithEntity(username, request.getUpdatedBy());
        return ResponseEntity.ok("Staff with username " + username + " has been activated successfully.");
    }

    @DeleteMapping("/deleteStaff/{username}")
    @Transactional
    public ResponseEntity<String> deleteStaffByUsername(@PathVariable String username) {
        staffService.deleteStaffWithEntity(username);
        return ResponseEntity.ok("Staff with username " + username + " has been deleted successfully.");
    }

    @GetMapping("/getDeActivatedStaffs")
    public ResponseEntity<List<StaffPanelDTO>> getDeActivatedStaffs(@RequestParam String role) {
        return staffService.getDeactivatedStaffs(role);
    }


    @GetMapping("/getStaffByUsername/{username}")
    public ResponseEntity<StaffProfileDTO> getStaffByUsername(@PathVariable String username) {
        return staffService.getStaffProfileDTOByUsername(username);
    }


//    @GetMapping("/getStaffByEmail/{email}")
//    public ResponseEntity<Staff> getStaffByEmail(@PathVariable String email) {
//        return staffService.getStaffByEmail(email);
//    }
//
//    @GetMapping("/getStaffByPhoneNo/{phoneNo}")
//    public ResponseEntity<Staff> getStaffByPhoneNo(@PathVariable String phoneNo) {
//        return staffService.getStaffByPhoneNo(phoneNo);
//    }

    @PostMapping("/saveStaffWithEntity")
    public ResponseEntity<String> saveStaffsWithEntity(@RequestBody StaffUserDTO staffUserDTO) {
        return staffService.saveStaffsWithEntity(staffUserDTO);
    }

//    @PostMapping("/saveSuperStaffWithEntity")
//    public ResponseEntity<String> saveSuperStaffsWithEntity(@RequestBody StaffUserDTO staffUserDTO) {
//        return staffService.saveSuperStaffsWithEntity(staffUserDTO);
//    }

//    @PostMapping("/saveStaffs")
//    public ResponseEntity<String> saveStaffs(@RequestBody List<Staff> staffData) {
//        return staffService.saveStaffs(staffData);
//    }

    @PutMapping("/updateStaffs")
    public ResponseEntity<String> updateStaffs(@RequestBody List<Staff> staffData) {
        return staffService.updateStaffs(staffData);
    }

    @GetMapping("/getStaffsWithAttendanceStatus")
    public ResponseEntity<List<StaffWithAttendanceDTO>> getStaffsWithAttendanceStatus(@RequestParam("date") LocalDate date) {
        return staffService.getStaffsWithAttendanceStatus(date);
    }

}
