package com.schooltech.sms.scheduler.sms_attendance;

import com.schooltech.sms.scheduler.sms_attendance.util.StudentAbsentSmsQuotaCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentAbsentSmsQuotaCountCronJob {

    private final StudentAbsentSmsQuotaCounter studentAbsentSmsQuotaCounter;

    @Scheduled(cron = "0 1 19 * * *", zone = "Asia/Kolkata")
    public void updateSmsQuotaForMahs() {
        studentAbsentSmsQuotaCounter.updateSmsQuotaForTenant("mahs");
    }

    @Scheduled(cron = "0 2 19 * * *", zone = "Asia/Kolkata")
    public void updateSmsQuotaForMahsp() {
        studentAbsentSmsQuotaCounter.updateSmsQuotaForTenant("mahsp");
    }
}