package com.schooltech.sms.service.tenant_data;

import com.schooltech.sms.dao.client.tenant_data.TenantDataRepository;
import com.schooltech.sms.entity.client.tenant_data.TenantData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
}