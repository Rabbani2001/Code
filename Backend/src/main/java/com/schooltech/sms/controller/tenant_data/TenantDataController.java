package com.schooltech.sms.controller.tenant_data;


import com.schooltech.sms.service.tenant_data.TenantDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}