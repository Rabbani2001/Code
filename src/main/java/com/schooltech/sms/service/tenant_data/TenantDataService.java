package com.schooltech.sms.service.tenant_data;

import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.dao.client.tenant_data.TenantDataRepository;
import com.schooltech.sms.entity.client.tenant_data.TenantData;
import com.schooltech.sms.utility.DateUtility;
import com.twilio.rest.microvisor.v1.App;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TenantDataService {

    @Autowired
    private TenantDataRepository tenantDataRepository;

    public ResponseEntity<Map<String, Integer>> getSmsQuotaDetails() {
        Optional<TenantData> tenantDataOptional = tenantDataRepository.findFirstBy();

        Map<String, Integer> smsQuota = tenantDataOptional
                .map(TenantData::getSmsQuota)
                .orElseGet(HashMap::new);

        int usedQuota = smsQuota.getOrDefault("usedSmsQuota", 0);
        int totalLimit = smsQuota.getOrDefault("totalSmsQuota", 0);

        int available = Math.max(totalLimit - usedQuota, 0);

        Map<String, Integer> result = new HashMap<>();
        result.put("used", usedQuota);
        result.put("available", available);
        result.put("total", totalLimit);

        return ResponseEntity.ok(result);
    }
    @Transactional(transactionManager = "clientTransactionManager")
    public Map<String, String> saveAdmissionNoPattern(Map<String, String> request) {
        String admissionNo = request.get(AppConstant.ADMISSION_NO);

        if (admissionNo == null || admissionNo.isBlank()) {
            throw new IllegalArgumentException("Admission number pattern cannot be empty");
        }

        TenantData tenantData = tenantDataRepository
                .findFirstBy()
                .orElseGet(() -> {
                    TenantData newTenantData = new TenantData();
                    newTenantData.setTimestamp(DateUtility.getCurrentTimeStamp());
                    return newTenantData;
                });

        // Create a new map instead of copying old values
        Map<String, String> pattern = new HashMap<>();
        pattern.put(AppConstant.ADMISSION_NO, admissionNo.trim());
        tenantData.setPattern(pattern);
        tenantDataRepository.save(tenantData);
        return pattern;
    }

    @Transactional(transactionManager = "clientTransactionManager", readOnly = true)
    public String getAdmissionPattern() {

        TenantData tenantData = tenantDataRepository
                .findFirstBy()
                .orElse(null);

        if (tenantData == null || tenantData.getPattern() == null) {
            return null;
        }

        return tenantData.getPattern().get(AppConstant.ADMISSION_NO);
    }
}