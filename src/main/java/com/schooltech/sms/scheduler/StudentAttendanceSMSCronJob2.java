//package com.schooltech.sms.scheduler;
//
//import com.schooltech.multitenant.TenantContext;
//import com.schooltech.sms.constant.ClientConfig;
//import com.schooltech.sms.controller.student.dto.StudentWithAttendanceDTO;
//import com.schooltech.sms.dao.master.TenantRepository;
//import com.schooltech.sms.entity.master.SchoolTenant;
//import com.schooltech.sms.service.student.StudentService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//@Slf4j
//@Component
//public class StudentAttendanceSMSCronJob2 {
//
//    private final TenantRepository tenantRepository;
//    private final StudentService studentService;
//
//    public StudentAttendanceSMSCronJob2(TenantRepository tenantRepository, StudentService studentService) {
//        this.tenantRepository = tenantRepository;
//        this.studentService = studentService;
//    }
//
//    // Change "10:30:00" to whatever time you want to test
//    // Cron format: second minute hour day month day-of-week
//    //@Scheduled(cron = "0 30 10 * * MON-SAT", zone = "Asia/Kolkata")
//    @Scheduled(cron = "0 * * * * MON-SAT", zone = "Asia/Kolkata")
//    public void sendAbsentSmsToParents() {
//        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
//
//        String currentTimeStr = LocalTime.now(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("HH:mm"));
//        if (!currentTimeStr.equals("21:27")) return;
//
//        List<SchoolTenant> allTenants = tenantRepository.findAll();
//        for (SchoolTenant tenant : allTenants) {
//            // Only run for tenants enabled in ClientConfig
//            if (!ClientConfig.isStudentAttendanceCronEnabledForTenant(tenant.getTenantCode())) {
//                continue;
//            }
//
//            //AppConstant.ATTENDANCE_CRONJOB_TIME
//            //This shoudl be calculated based on school start time time +2hrs
//
//            // if current time is not equal to the configured cron job time, exit early
//            //
//
//
//            try {
//                TenantContext.setCurrentTenant(tenant.getTenantId());
//
//                //Fetching all absent student
//                ResponseEntity<List<StudentWithAttendanceDTO>> response =
//                        studentService.getStudentsByClassWithAttendence("all", today);
//                List<StudentWithAttendanceDTO> absentStudents = response.getBody().stream()
//                        .filter(s -> "absent".equalsIgnoreCase(s.getStatus()))
//                        .toList();
//                log.info("Initiating Sednding smsess");
//
//                for (StudentWithAttendanceDTO absent : absentStudents) {
//                    // You'll need parent phoneNo — currently DTO has parentUsername, not phoneNo directly.
//                    // You'd look up the parent's phone via a User/Parent repository call here,
//                    // then call your SMS service with (phoneNo, fullName, today).
//                    log.info("Would send SMS for absent student: {} (parentUsername: {})",
//                            absent.getFullName(), absent.getParentUsername());
//                }
//
//            } catch (Exception e) {
//                log.error("Error sending absent SMS for tenant [{}]: {}", tenant.getTenantCode(), e.getMessage(), e);
//            } finally {
//                TenantContext.clear();
//            }
//        }
//    }
//}