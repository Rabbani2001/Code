package com.schooltech.sms.scheduler.sms_attendance;

import com.schooltech.sms.constant.ClientConfig;
import com.schooltech.sms.dao.master.TenantRepository;
import com.schooltech.sms.entity.master.SchoolTenant;
import com.schooltech.sms.scheduler.sms_attendance.util.StudentAttendanceSendSMS;
import com.schooltech.sms.service.student.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class TESTStudentAttendanceSMSCronJob {

    private final TenantRepository tenantRepository;
    private final StudentService studentService;

    @Autowired
    private StudentAttendanceSendSMS studentAttendanceSendSMS;


    public TESTStudentAttendanceSMSCronJob(TenantRepository tenantRepository, StudentService studentService) {
        this.tenantRepository = tenantRepository;
        this.studentService = studentService;
    }

    // Change "10:30:00" to whatever time you want to test
    // Cron format: second minute hour day month day-of-week
    //@Scheduled(cron = "0 30 10 * * MON-SAT", zone = "Asia/Kolkata")
    @Scheduled(cron = "0 0 10 * * MON-SAT", zone = "Asia/Kolkata")
    public void sendAbsentSmsToParents() {
        if (!ClientConfig.isStudentAttendanceSMSCronEnabledForTenant("test")) {
            return;
        }

        //Guard for Testing
        //String currentTimeStr = LocalTime.now(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("HH:mm"));
        //if (!currentTimeStr.equals("22:56")) return;

        Optional<SchoolTenant> schoolTenant = tenantRepository.findByTenantCode("test");
        String schoolName = "";
        if (schoolTenant.isPresent())
            schoolName = schoolTenant.get().getTenantName();

        studentAttendanceSendSMS.initiateSendSMSForTenant("test", "stclient_default", schoolName);
    }
}