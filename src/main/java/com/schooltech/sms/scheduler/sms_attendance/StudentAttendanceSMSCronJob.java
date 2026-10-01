package com.schooltech.sms.scheduler.sms_attendance;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.sms.constant.ClientConfig;
import com.schooltech.sms.dao.client.tenant_data.TenantDataRepository;
import com.schooltech.sms.dao.master.TenantRepository;
import com.schooltech.sms.entity.client.tenant_data.TenantData;
import com.schooltech.sms.entity.master.SchoolTenant;
import com.schooltech.sms.scheduler.sms_attendance.util.StudentAttendanceSendSMS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class StudentAttendanceSMSCronJob {

    private final TenantRepository tenantRepository;

    @Autowired
    private StudentAttendanceSendSMS studentAttendanceSendSMS;

    @Autowired
    private TenantDataRepository tenantDataRepository;

    public StudentAttendanceSMSCronJob(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    // Cron format: second minute hour day month day-of-week
    @Scheduled(cron = "0 1 10 * * MON-SAT", zone = "Asia/Kolkata")
    public void sendAbsentSmsToParentsForMahs() {
        sendAbsentSmsToParents("mahs", "stclient_mahs");
    }

    @Scheduled(cron = "0 0 10 * * MON-SAT", zone = "Asia/Kolkata")
    public void sendAbsentSmsToParentsForMahsp() {
        sendAbsentSmsToParents("mahsp", "stclient_mahsp");
    }

    private void sendAbsentSmsToParents(String tenantCode, String dbName) {
        if (!ClientConfig.isStudentAttendanceSMSCronEnabledForTenant(tenantCode)) {
            return;
        }

        Optional<SchoolTenant> schoolTenant = tenantRepository.findByTenantCode(tenantCode);
        if (schoolTenant.isEmpty()) {
            log.error("Tenant not found for code: {}", tenantCode);
            return;
        }
        SchoolTenant tenant = schoolTenant.get();
        String schoolName = tenant.getTenantName();

        try {
            TenantContext.setCurrentTenant(tenant.getTenantId());

            Optional<TenantData> tenantDataOptional = tenantDataRepository.findFirstBy();

            Map<String, Integer> smsQuota = tenantDataOptional
                    .map(TenantData::getSmsQuota)
                    .orElseGet(HashMap::new);

            int usedQuota = smsQuota.getOrDefault("usedSmsQuota", 0);
            int totalQuota = smsQuota.getOrDefault("totalSmsQuota", 0);

            if (usedQuota >= totalQuota) {
                log.info("[{}] SMS quota reached: {} >= {} — skipping SMS send", tenantCode, usedQuota, totalQuota);
                return;
            }
        } catch (Exception e) {
            log.error("SMS quota check failed for tenant [{}] → {}", tenantCode, e.getMessage(), e);
            return;
        } finally {
            TenantContext.clear();
        }

        studentAttendanceSendSMS.initiateSendSMSForTenant(tenantCode, dbName, schoolName);
    }
}