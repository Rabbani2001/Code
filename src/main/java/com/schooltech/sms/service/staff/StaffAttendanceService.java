package com.schooltech.sms.service.staff;


import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.controller.staff.dto.MarkStaffAttendanceDTO;
import com.schooltech.sms.dao.client.attendence.StaffAttendanceRepository;
import com.schooltech.sms.dao.client.staff.StaffRepository;
import com.schooltech.sms.entity.client.attendence.AttendanceAuditEntry;
import com.schooltech.sms.entity.client.attendence.StaffAttendance;
import com.schooltech.sms.entity.client.staff.Staff;
import com.schooltech.sms.exception.StaffNotFoundException;
import com.schooltech.sms.service.communication.EmailService;
import com.schooltech.sms.utility.AttendanceAuditUtil;
import com.schooltech.sms.utility.DateUtility;
import jakarta.persistence.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StaffAttendanceService {
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthService authService;

    @Autowired
    private StaffAttendanceRepository staffAttendenceRepository;

    public ResponseEntity<List<MarkStaffAttendanceDTO>> markAttendenceStaff(List<StaffAttendance> staffAttendances) {  //sdn one object if attendence to be marked for single staff
        List<MarkStaffAttendanceDTO> staffFullNameList = new ArrayList<>();
        staffAttendances.stream().forEach(staffAttendance -> {
            //checking staff is valid or not
            Optional<Staff> staff = staffRepository.findByUsername(staffAttendance.getUsername());
            staff.orElseThrow(() -> new StaffNotFoundException("You are marking attendance for invalid Staff"));
            MarkStaffAttendanceDTO markStaffAttendanceDTO = new MarkStaffAttendanceDTO();
            markStaffAttendanceDTO.setFullName(staff.get().getFullName());
            markStaffAttendanceDTO.setRole(staff.get().getRole());
            staffFullNameList.add(markStaffAttendanceDTO);
            //check for Existing Attendance
            StaffAttendance staffAttendanceDB = staffAttendenceRepository.findStaffAttendenceByStaffIdAndDate(staffAttendance.getUsername(), staffAttendance.getDate());
            List<AttendanceAuditEntry> existingAudit = null;
            if (staffAttendanceDB != null) {
                staffAttendance.setId(staffAttendanceDB.getId());
                existingAudit = staffAttendanceDB.getUpdatedBy();
                if (staffAttendance.getMetaData() == null) {
                    staffAttendance.setMetaData(staffAttendanceDB.getMetaData());
                }
            }

            List<AttendanceAuditEntry> incoming = staffAttendance.getUpdatedBy();
            String action = "update";
            String updatedBy = "unknown";
            if (incoming != null && !incoming.isEmpty()) {
                AttendanceAuditEntry entry = incoming.get(0);
                if (entry.getAction() != null) {
                    action = entry.getAction();
                } else if (staffAttendance.getStatus() != null) {
                    action = staffAttendance.getStatus().name();
                }
                if (entry.getUpdatedBy() != null) {
                    updatedBy = entry.getUpdatedBy();
                }
            }

            staffAttendance.setUpdatedBy(AttendanceAuditUtil.append(existingAudit, action, updatedBy));
            staffAttendance.setTimestamp(DateUtility.getCurrentTimeStamp());
        });
        staffAttendenceRepository.saveAllAndFlush(staffAttendances);
        return ResponseEntity.ok(staffFullNameList);
    }

    public ResponseEntity<String> updateStaffsAttendance(List<StaffAttendance> staffAttendanceList) {
        staffAttendanceList.forEach(staffAttendance -> {
            staffAttendenceRepository.findStaffAttendenceByStaffIdAndDate2(staffAttendance.getUsername(), staffAttendance.getDate()).ifPresentOrElse(existingStaffAttendance -> {
                updateNonNullFields(existingStaffAttendance, staffAttendance);
                List<AttendanceAuditEntry> incoming = staffAttendance.getUpdatedBy();
                String action = "update";
                String updatedBy = "unknown";
                if (incoming != null && !incoming.isEmpty()) {
                    AttendanceAuditEntry entry = incoming.get(0);
                    if (entry.getAction() != null) {
                        action = entry.getAction();
                    } else if (staffAttendance.getApproved() != null) {
                        action = "approval:" + staffAttendance.getApproved().name();
                    } else if (staffAttendance.getStatus() != null) {
                        action = staffAttendance.getStatus().name();
                    }
                    if (entry.getUpdatedBy() != null) {
                        updatedBy = entry.getUpdatedBy();
                    }
                }

                existingStaffAttendance.setUpdatedBy(
                        AttendanceAuditUtil.append(existingStaffAttendance.getUpdatedBy(), action, updatedBy));
                existingStaffAttendance.setTimestamp(DateUtility.getCurrentTimeStamp());
                staffAttendenceRepository.save(existingStaffAttendance);
            }, () -> {
                throw new RuntimeException("Staff Attendance: " + staffAttendance.getUsername() + " not present. Aborting Update");
            });
        });
        return ResponseEntity.ok("Staff Attendance Data Updated");
    }

    private void updateNonNullFields(StaffAttendance existingStaffAttendance, StaffAttendance newStaffAttendance) {
        if (newStaffAttendance.getStatus() != null)
            existingStaffAttendance.setStatus(newStaffAttendance.getStatus());
        if (newStaffAttendance.getCheckInTime() != null)
            existingStaffAttendance.setCheckInTime(newStaffAttendance.getCheckInTime());
        if (newStaffAttendance.getCheckOutTime() != null)
            existingStaffAttendance.setCheckOutTime(newStaffAttendance.getCheckOutTime());
        if (newStaffAttendance.getApproved() != null)
            existingStaffAttendance.setApproved(newStaffAttendance.getApproved());
    }

    public ResponseEntity<StaffAttendance> getStaffAttendence(String staffId, LocalDate date) {
        try {
            StaffAttendance staffAttendance = staffAttendenceRepository.findStaffAttendenceByStaffIdAndDate(staffId, date);
            if (staffAttendance == null) {
                throw new StaffNotFoundException("Staff attendance not found");
            }
            return new ResponseEntity<>(staffAttendance, HttpStatus.OK);
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("Multiple attendance records found for staff " + staffId + " on " + date, e);
        } catch (StaffNotFoundException e) {
            throw e; // Re-throw custom exception to allow custom handler to catch it
        } catch (Exception e) {
            // Catch any unexpected errors (e.g., DB connection issues)
            throw new RuntimeException("An unexpected error occurred while fetching staff attendance", e);
        }
    }

    public ResponseEntity<List<StaffAttendance>> getStaffAttendenceHistory(String staffId, LocalDate startDate, LocalDate endDate) {
        List<StaffAttendance> staffAttendances = staffAttendenceRepository.findAllAttendenceBetweenDates(staffId, startDate, endDate);
        return new ResponseEntity<>(staffAttendances, HttpStatus.OK);
    }


    public ResponseEntity<List<StaffAttendance>> getStaffAttendencesByDate(LocalDate date) {
        List<StaffAttendance> staffAttendances = staffAttendenceRepository.findByDate(date);
        return new ResponseEntity<>(staffAttendances, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteStaffAttendancesForDate(LocalDate date) {
        try {
            List<StaffAttendance> staffAttendances = staffAttendenceRepository.findByDate(date);
            staffAttendances.stream().forEach((staffAttendance) -> {
                staffAttendenceRepository.delete(staffAttendance);
            });
            return new ResponseEntity<>("Staff Attendances for Date: " + date + " has been deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable To Delete Staff Attendances for Date: " + date, HttpStatus.NOT_FOUND);
        }
    }
}
