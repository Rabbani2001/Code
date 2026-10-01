package com.schooltech.sms.scheduler.sms_attendance.util;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.sms.dao.client.attendence.StudentAttendenceRepository;
import com.schooltech.sms.dao.client.tenant_data.TenantDataRepository;
import com.schooltech.sms.dao.master.TenantRepository;
import com.schooltech.sms.entity.client.attendence.StudentAttendance;
import com.schooltech.sms.entity.client.tenant_data.TenantData;
import com.schooltech.sms.entity.master.SchoolTenant;
import com.schooltech.sms.utility.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentAbsentSmsQuotaCounter {

    private final TenantRepository tenantRepository;
    private final StudentAttendenceRepository studentAttendenceRepository;
    private final TenantDataRepository tenantDataRepository;

    // Considers attendance records with check-in time before 10:02 AM
    private static final LocalTime CUTOFF_TIME = LocalTime.of(10, 2, 0);

    /**
     * Looks up the tenant, switches into its DB context, counts today's absent
     * students (checked in before CUTOFF_TIME), and adds that count onto
     * usedSmsQuota in tenant_data. Manages its own TenantContext internally —
     * caller does not need to set/clear it.
     */
    public void updateSmsQuotaForTenant(String tenantCode) {
        Optional<SchoolTenant> schoolTenantOptional = tenantRepository.findByTenantCode(tenantCode);
        if (schoolTenantOptional.isEmpty()) {
            log.error("Tenant not found for code: {}", tenantCode);
            return;
        }
        SchoolTenant tenant = schoolTenantOptional.get();

        try {
            TenantContext.setCurrentTenant(tenant.getTenantId());
            updateUsedSmsQuota(tenantCode);
        } catch (Exception e) {
            log.error("SMS quota cron failed for tenant [{}] → {}", tenantCode, e.getMessage(), e);
        } finally {
            TenantContext.clear();
        }
    }

    private void updateUsedSmsQuota(String tenantCode) {
        Optional<TenantData> tenantDataOptional = tenantDataRepository.findFirstBy();
        if (tenantDataOptional.isEmpty()) {
            log.error("tenant_data row not found for tenant: {}", tenantCode);
            return;
        }
        TenantData tenantData = tenantDataOptional.get();

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        long todaysAbsentCount = studentAttendenceRepository.countByStatusAndDateAndCheckInTimeBefore(
                StudentAttendance.Status.absent, today, CUTOFF_TIME);

        Map<String, Integer> smsQuota = tenantData.getSmsQuota() != null
                ? new HashMap<>(tenantData.getSmsQuota())
                : new HashMap<>();

        int existingUsed = smsQuota.getOrDefault("usedSmsQuota", 0);
        int updatedUsed = existingUsed + (int) todaysAbsentCount;
        smsQuota.put("usedSmsQuota", updatedUsed);

        tenantData.setSmsQuota(smsQuota);
        tenantData.setTimestamp(DateUtility.getCurrentTimeStamp());
        tenantDataRepository.save(tenantData);

        int totalQuota = smsQuota.getOrDefault("totalSmsQuota", 0);
        log.info("[{}] SMS quota: {} (+{} absent today) → {} / {}", tenantCode, existingUsed, todaysAbsentCount, updatedUsed, totalQuota);
    }
}