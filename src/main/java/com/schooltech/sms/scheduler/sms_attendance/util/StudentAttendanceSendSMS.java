package com.schooltech.sms.scheduler.sms_attendance.util;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.controller.communication.dto.RecipientDTO;
import com.schooltech.sms.controller.student.dto.StudentWithAttendanceDTO;
import com.schooltech.sms.dao.master.TenantRepository;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.service.communication.SmsService;
import com.schooltech.sms.service.student.StudentService;
import com.schooltech.sms.utility.DateUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StudentAttendanceSendSMS {
    private final StudentService studentService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private AuthService authService;

    public StudentAttendanceSendSMS(TenantRepository tenantRepository, StudentService studentService) {
        this.studentService = studentService;
    }

    public void initiateSendSMSForTenant(String tenantCode, String tenantId, String schoolName) {
        log.info("================ Initiating Sending sms's for " + tenantCode);
        try {
            TenantContext.setCurrentTenant(tenantId);
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
            //Fetching all absent student
            ResponseEntity<List<StudentWithAttendanceDTO>> response =
                    studentService.getStudentsByClassWithAttendence("all", today);
            List<StudentWithAttendanceDTO> absentStudents = response.getBody().stream()
                    .filter(s -> "absent".equalsIgnoreCase(s.getStatus()))
                    .toList();
            List<RecipientDTO> recipientDTOList = new ArrayList<>();
            for (StudentWithAttendanceDTO absent : absentStudents) {
                RecipientDTO recipientDTO = new RecipientDTO();
                recipientDTO.setDate(DateUtility.getCurrentDate().toString());
                User user = authService.getUserByUsername(absent.getParentUsername());
                recipientDTO.setMobile("91" + user.getPhoneNo());
                recipientDTO.setStudentName(absent.getFullName());
                recipientDTO.setSchoolName(schoolName);
                recipientDTOList.add(recipientDTO);
                log.info("===============Sending SMS for Absent student: {} ,{},{},{}",
                        absent.getFullName(), "91" + user.getPhoneNo(), schoolName, DateUtility.getCurrentDate());
            }
            if (!recipientDTOList.isEmpty()) {
                smsService.sendMessage(recipientDTOList);
            }
        } catch (Exception e) {
            log.error("Error sending absent SMS for tenant [{}]: {}", tenantCode, e.getMessage(), e);
        } finally {
            TenantContext.clear();
        }
    }
}
