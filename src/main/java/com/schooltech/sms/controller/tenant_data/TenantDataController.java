package com.schooltech.sms.controller.tenant_data;


import com.schooltech.sms.service.tenant_data.TenantDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Validated
public class TenantDataController {
    @Autowired
    private TenantDataService tenantDataService;

    @GetMapping("/getSmsQuotaDetails")
    public ResponseEntity<Map<String, Integer>> getSmsQuotaDetails() {
        return tenantDataService.getSmsQuotaDetails();
    }

    @PostMapping("/saveAdmissionNoPattern")
    public ResponseEntity<Map<String, String>> saveAdmissionNoPattern(@RequestBody Map<String, String> request) {
        Map<String, String> response = tenantDataService.saveAdmissionNoPattern(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAdmissionPattern")
    public ResponseEntity<String> getAdmissionPattern() {
        String admissionPattern = tenantDataService.getAdmissionPattern();
        return ResponseEntity.ok(admissionPattern);
    }

}