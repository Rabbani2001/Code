package com.schooltech.sms.controller.staff;


import com.schooltech.sms.controller.staff.dto.MarkStaffAttendanceDTO;
import com.schooltech.sms.entity.client.attendence.StaffAttendance;
import com.schooltech.sms.service.staff.StaffAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
public class StaffAttendanceController {
    @Autowired
    private StaffAttendanceService staffAttendanceService;

    @GetMapping("/getStaffAttendance/{username}")
    public ResponseEntity<StaffAttendance> getStaffAttendence(@PathVariable String username, @RequestParam("date") LocalDate date) {
        return staffAttendanceService.getStaffAttendence(username, date);
    }

    @GetMapping("/getStaffAttendanceHistory/{staffId}")
    public ResponseEntity<List<StaffAttendance>> getStaffAttendenceHistory(@PathVariable String staffId, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return staffAttendanceService.getStaffAttendenceHistory(staffId, startDate, endDate);
    }


    @GetMapping("/getStaffAttendancesByDate/{date}")
    public ResponseEntity<List<StaffAttendance>> getStaffAttendencesByDate(@PathVariable LocalDate date) {
        return staffAttendanceService.getStaffAttendencesByDate(date);
    }

    @DeleteMapping("/deleteStaffAttendancesForDate")
    public ResponseEntity<String> deleteStaffAttendancesForDate(@RequestParam LocalDate date) {
        return staffAttendanceService.deleteStaffAttendancesForDate(date);
    }

    @Transactional  //transaction is needed as it is post api call
    @PostMapping("/markStaffAttendance")
    public ResponseEntity<List<MarkStaffAttendanceDTO>> markStaffAttendance(@RequestBody List<StaffAttendance> staffAttendances) {  //sdn one object if attendence to be marked for single staff
        return staffAttendanceService.markAttendenceStaff(staffAttendances);
    }


    @PutMapping("/updateStaffsAttendance")
    @Transactional
    public ResponseEntity<String> updateStaffsAttendance(@RequestBody List<StaffAttendance> staffAttendanceList) {
        return staffAttendanceService.updateStaffsAttendance(staffAttendanceList);
    }

}