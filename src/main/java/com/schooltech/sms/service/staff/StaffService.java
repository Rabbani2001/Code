package com.schooltech.sms.service.staff;


import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.controller.staff.dto.StaffPanelDTO;
import com.schooltech.sms.controller.staff.dto.StaffProfileDTO;
import com.schooltech.sms.controller.staff.dto.StaffUserDTO;
import com.schooltech.sms.controller.staff.dto.StaffWithAttendanceDTO;
import com.schooltech.sms.dao.client.staff.StaffRepository;
import com.schooltech.sms.dao.client.userTrash.UserTrashRepository;
import com.schooltech.sms.entity.client.attendence.StaffAttendance;
import com.schooltech.sms.entity.client.payment.bank.BankDetails;
import com.schooltech.sms.entity.client.staff.Staff;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.exception.StaffNotFoundException;
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
import java.util.*;

import static com.schooltech.sms.constant.AppConstant.STAFF_ELIGIBLE_ROLES;

@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthService authService;
    @Autowired
    private UsernameUtility usernameUtility;
    @Autowired
    private BankDetailsService bankDetailsService;

    @Autowired
    private UserTrashRepository userTrashRepository;


    private List<StaffPanelDTO> mapToStaffPanelDTOs(List<Staff> staffs) {
        List<StaffPanelDTO> staffPanelDTOList = new ArrayList<>();
        staffs.forEach(staff -> {
            StaffPanelDTO staffPanelDTO = new StaffPanelDTO();
            User user = authService.getUserByUsername(staff.getUsername());
            staffPanelDTO.setUsername(staff.getUsername());
            staffPanelDTO.setGender(staff.getGender());
            staffPanelDTO.setRole(staff.getRole());
            staffPanelDTO.setFullName(staff.getFullName());
            staffPanelDTO.setEmployeeId(staff.getEmployeeId());
            staffPanelDTO.setDesignation(staff.getDesignation());
            staffPanelDTO.setDocuments(staff.getDocuments());
            staffPanelDTO.setPhoneNo(user.getPhoneNo());
            staffPanelDTOList.add(staffPanelDTO);
        });
        return staffPanelDTOList;
    }

    public ResponseEntity<List<StaffPanelDTO>> getAllStaffs() {
        List<Staff> staffs = staffRepository.findByUsernameNotSuperadmin();
        return new ResponseEntity<>(mapToStaffPanelDTOs(staffs), HttpStatus.OK);
    }

    public ResponseEntity<List<StaffPanelDTO>> getStaffs(String role) {
        List<Staff> staffs = staffRepository.findByRoleExcludingSuperadmin(role);
        return new ResponseEntity<>(mapToStaffPanelDTOs(staffs), HttpStatus.OK);
    }

    public ResponseEntity<List<StaffPanelDTO>> getDeactivatedStaffs(String role) {

        List<StaffPanelDTO> staffPanelDTOList = new ArrayList<>();
        List<Staff> staffs = staffRepository.getDeActivateStaffs(role);
        staffs.forEach(staff -> {
            StaffPanelDTO staffPanelDTO = new StaffPanelDTO();
            //User user = authService.getUserByUsername(staff.getUsername());
            staffPanelDTO.setUsername(staff.getUsername());
            staffPanelDTO.setFullName(staff.getFullName());
            staffPanelDTO.setEmployeeId(staff.getEmployeeId());
            staffPanelDTO.setDesignation(staff.getDesignation());
            staffPanelDTO.setDocuments(staff.getDocuments());
            //staffPanelDTO.setPhoneNo(user.getPhoneNo());
            staffPanelDTOList.add(staffPanelDTO);
        });
        return new ResponseEntity<>(staffPanelDTOList, HttpStatus.OK);
    }

    public void deActivateStaffWithEntity(String username, String updatedBy) {
        if (!staffRepository.existsByUsername(username)) {
            throw new RuntimeException("Staff with username " + username + " does not exist.");
        }
        staffRepository.deActivateByUsername(username);
        authService.deActivateUserByUsername(username, updatedBy);
    }

    public void deleteStaffWithEntity(String username) {
        Optional<Staff> staffOptional = staffRepository.findByUsernameDeactivatedStatus(username);

        if (staffOptional.isEmpty()) {
            throw new RuntimeException("Staff with username " + username + " does not exist.");
        }

        staffRepository.deleteByUsername(username);
        userTrashRepository.deleteByUsername(username);
    }

    public void activateStaffWithEntity(String username, String updatedBy) {
        staffRepository.activateByUsername(username);
        authService.activateUserByUsername(username, updatedBy);
    }

    public ResponseEntity<StaffProfileDTO> getStaffProfileDTOByUsername(String staffId) {
        Optional<Staff> staffOptional = staffRepository.findByUsername(staffId);
        Staff staff = staffOptional.orElseThrow(() -> new StaffNotFoundException("Staff Not Available"));
        User user = authService.getUserByUsername(staffId);
        BankDetails bankDetails = bankDetailsService.getBankDetails(staffId);
        StaffProfileDTO staffProfileDTO = new StaffProfileDTO();
        // Teacher mapping
        staffProfileDTO.setUsername(staff.getUsername());
        staffProfileDTO.setRole(staff.getRole());
        staffProfileDTO.setEmployeeId(staff.getEmployeeId());
        staffProfileDTO.setFullName(staff.getFullName());
        staffProfileDTO.setGender(staff.getGender());
        staffProfileDTO.setAadharNo(staff.getAadharNo());
        staffProfileDTO.setPanNo(staff.getPanNo());
        staffProfileDTO.setClassNames(staff.getClassNames());
        staffProfileDTO.setFatherName(staff.getFatherName());
        staffProfileDTO.setMotherName(staff.getMotherName());
        staffProfileDTO.setGuardianName(staff.getGuardianName());
        staffProfileDTO.setQualification(staff.getQualification());
        staffProfileDTO.setDesignation(staff.getDesignation());
        staffProfileDTO.setDob(staff.getDob());
        staffProfileDTO.setJoiningDate(staff.getJoiningDate());
        staffProfileDTO.setPermanentAddress(staff.getPermanentAddress());
        staffProfileDTO.setCurrentAddress(staff.getCurrentAddress());
        staffProfileDTO.setBusRoute(staff.getBusRoute());
        staffProfileDTO.setDocuments(staff.getDocuments());
        // User mapping
        staffProfileDTO.setEmail(user.getEmail());
        staffProfileDTO.setPhoneNo(user.getPhoneNo());
        staffProfileDTO.setAltPhoneNo(user.getAltPhoneNo());
        staffProfileDTO.setUpdatedBy(user.getUpdatedBy());
        //
        staffProfileDTO.setBankDetails(bankDetails);

        return new ResponseEntity<>(staffProfileDTO, HttpStatus.OK);
    }


    public Staff getStaffByUsername(String staffId) {
        Optional<Staff> staffOptional = staffRepository.findByUsername(staffId);
        return staffOptional.orElseThrow(() -> new StaffNotFoundException("Staff Not Available"));
    }

    @Transactional
    public ResponseEntity<String> saveStaffsWithEntity(StaffUserDTO staffUserDTO) {
        try {
            //saving users & staff
            String staffUsername = "";
            if (staffUserDTO.getUser() != null && staffUserDTO.getStaff() != null) {
                String role = staffUserDTO.getStaff().getRole();
                if (!STAFF_ELIGIBLE_ROLES.contains(staffUserDTO.getStaff().getRole())) {
                    throw new RuntimeException("Creation of Staff Failed as " + role + " is not eligible for staff");
                }
                staffUsername = usernameUtility.generateUsername(role);
                //save in user table for staff
                User staffUser = staffUserDTO.getUser();
                staffUser.setUsername(staffUsername);
                List<User> userList = new ArrayList<>();
                userList.add(staffUser);
                authService.saveAllUser(userList);
                //Save in Teacher table
                Staff staff = staffUserDTO.getStaff();
                staff.setUsername(staffUsername);
                Map<String, String> staffDocuments = new HashMap<>();
                staff.setDocuments(staffDocuments);
                List<Staff> staffList = new ArrayList<>();
                staffList.add(staff);
                saveStaffs(staffList);
            } else {
                throw new RuntimeException("Creation of Staff with other Entity Failed");
            }
            //emailService.sendCredentialsEmail(staff.getEmail(), staff.getUsername());
            return ResponseEntity.ok("Staff with Entity added successfully");
        } catch (Exception e) {
            throw new RuntimeException("Creation of Staff with other Entity Failed");
        }
    }

    public ResponseEntity<String> saveStaffs(List<Staff> staffData) {
        staffData.forEach(staff -> {
            staffRepository.findByUsername(staff.getUsername()).ifPresentOrElse(existingStaff -> {
                throw new RuntimeException("Staff: " + staff.getUsername() + " already Present. Aborting Save");
            }, () -> {
                staff.setTimestamp(DateUtility.getCurrentTimeStamp());
                User user = authService.getUserByUsername(staff.getUsername());
                emailService.sendCredentialsEmail(user.getEmail(), staff.getUsername());
            });
        });
        staffRepository.saveAll(staffData);
        return ResponseEntity.ok("Staff Data Saved");
    }

    public ResponseEntity<String> updateStaffs(List<Staff> staffData) {
        staffData.forEach(staff -> {
            staffRepository.findByUsername(staff.getUsername()).ifPresentOrElse(existingStaff -> {
                updateNonNullFields(existingStaff, staff);
                existingStaff.setTimestamp(DateUtility.getCurrentTimeStamp());
                staffRepository.save(existingStaff);
            }, () -> {
                throw new RuntimeException("Staff: " + staff.getUsername() + " not present. Aborting Update");
            });
        });

        return ResponseEntity.ok("Staff Data Updated");
    }


    private void updateNonNullFields(Staff existingStaff, Staff newStaff) {
        if (newStaff.getEmployeeId() != null) existingStaff.setEmployeeId(newStaff.getEmployeeId());
        if (newStaff.getFullName() != null) existingStaff.setFullName(newStaff.getFullName());
        if (newStaff.getGender() != null) existingStaff.setGender(newStaff.getGender());
        if (newStaff.getAadharNo() != null) existingStaff.setAadharNo(newStaff.getAadharNo());
        if (newStaff.getPanNo() != null) existingStaff.setPanNo(newStaff.getPanNo());
        if (newStaff.getClassNames() != null) existingStaff.setClassNames(newStaff.getClassNames());
        if (newStaff.getFatherName() != null) existingStaff.setFatherName(newStaff.getFatherName());
        if (newStaff.getMotherName() != null) existingStaff.setMotherName(newStaff.getMotherName());
        if (newStaff.getGuardianName() != null) existingStaff.setGuardianName(newStaff.getGuardianName());
        if (newStaff.getQualification() != null) existingStaff.setQualification(newStaff.getQualification());
        if (newStaff.getDesignation() != null) existingStaff.setDesignation(newStaff.getDesignation());
        if (newStaff.getDob() != null) existingStaff.setDob(newStaff.getDob());
        if (newStaff.getPermanentAddress() != null) existingStaff.setPermanentAddress(newStaff.getPermanentAddress());
        if (newStaff.getCurrentAddress() != null) existingStaff.setCurrentAddress(newStaff.getCurrentAddress());
        if (newStaff.getJoiningDate() != null) existingStaff.setJoiningDate(newStaff.getJoiningDate());
        if (newStaff.getBusRoute() != null) existingStaff.setBusRoute(newStaff.getBusRoute());
        if (newStaff.getDocuments() != null) existingStaff.setDocuments(newStaff.getDocuments());
    }

//    @Transactional
//    public void updateStaffPhoto(String username, String newPhotoUrl) {
//        staffRepository.findByUsername(username).ifPresent(staff -> {
//            staff.setPhotoUrl(newPhotoUrl);
//            staff.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
//            staffRepository.save(staff);
//        });
//    }

    public boolean isAnyStaffPresentInClass(String className) {
        return staffRepository.findAll()
                .stream()
                .anyMatch(staff ->
                        staff.getClassNames() != null &&
                                staff.getClassNames()
                                        .stream()
                                        .anyMatch(c -> c.equalsIgnoreCase(className))
                );
    }


    public ResponseEntity<List<StaffWithAttendanceDTO>> getStaffsWithAttendanceStatus(LocalDate date) {
        List<Object[]> result = staffRepository.findStaffsWithAttendanceStatusExcludingSuperadmin(date);
        List<StaffWithAttendanceDTO> staffWithAttendanceDTOList = result.stream()
                .map(objects -> {
                    Staff staff = (Staff) objects[0];
                    StaffAttendance attendance = (StaffAttendance) objects[1];

                    return getStaffProfileDTO(staff, attendance);
                })
                .toList();
        return ResponseEntity.ok(staffWithAttendanceDTOList);
    }

    private StaffWithAttendanceDTO getStaffProfileDTO(Staff staff, StaffAttendance attendance) {
        StaffWithAttendanceDTO staffWithAttendanceDTO = new StaffWithAttendanceDTO();
        // ---------------- Staff fields ----------------
        staffWithAttendanceDTO.setUsername(staff.getUsername());
        staffWithAttendanceDTO.setFullName(staff.getFullName());
        staffWithAttendanceDTO.setEmployeeId(staff.getEmployeeId());
        staffWithAttendanceDTO.setDocuments(staff.getDocuments());
        staffWithAttendanceDTO.setGender(staff.getGender());
        staffWithAttendanceDTO.setClassNames(staff.getClassNames());
        staffWithAttendanceDTO.setRole(staff.getRole());
        // ---------------- Attendance fields ----------------
        if (attendance != null) {
            staffWithAttendanceDTO.setStatus(attendance.getStatus() != null ? attendance.getStatus().toString() : null);
            staffWithAttendanceDTO.setApproved(attendance.getApproved() != null ? attendance.getApproved().toString() : null);
            staffWithAttendanceDTO.setCheckInTime(attendance.getCheckInTime() != null ? attendance.getCheckInTime().toString() : null);
            staffWithAttendanceDTO.setCheckOutTime(attendance.getCheckOutTime() != null ? attendance.getCheckOutTime().toString() : null);
        }
        return staffWithAttendanceDTO;
    }


}