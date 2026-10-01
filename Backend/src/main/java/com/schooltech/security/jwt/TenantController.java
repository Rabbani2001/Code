package com.schooltech.security.jwt;

import com.schooltech.security.jwt.service.TenantService;
import com.schooltech.sms.entity.master.SchoolTenant;
import com.schooltech.sms.entity.master.SchoolTenantDBConfig;
import com.schooltech.sms.utility.DateUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class TenantController {
    private Logger logger = LoggerFactory.getLogger(TenantController.class);
    @Autowired
    private TenantService tenantService;

    // NOT IN USE
    @PostMapping("/save-tenant")
    public ResponseEntity<String> saveSchoolTenant(@RequestBody SchoolTenant schoolTenant) {
        return tenantService.saveSchoolTenant(schoolTenant);
    }

    // NOT IN USE
    @PutMapping("/update-tenant")
    public ResponseEntity<String> updateSchoolTenant(@RequestBody SchoolTenant schoolTenant) {
        return tenantService.updateSchoolTenant(schoolTenant);
    }

    @PutMapping("/updateSchoolTenantByTenantId")
    @Transactional
    public ResponseEntity<String> updateSchoolTenantByTenantId(@RequestBody SchoolTenant schoolTenant) {
        return tenantService.updateSchoolTenantByTenantId(schoolTenant);
    }

    // NOT IN USE
    @GetMapping("/get-tenant/{tenantCode}")
    public SchoolTenant getSchoolTenant(@PathVariable String tenantCode) {
        return tenantService.getSchoolTenant(tenantCode);
    }

    @GetMapping("/getTenantByTenantId/{tenantId}")
    public SchoolTenant getTenantByTenantId(@PathVariable String tenantId) {
        return tenantService.getSchoolTenantByTenantId(tenantId);
    }

    // NOT IN USE
    @PostMapping("/save-tenant-dbconfig")
    public ResponseEntity<String> saveSchoolTenantConfig(@RequestBody SchoolTenantDBConfig schoolTenantDBConfig) {
        schoolTenantDBConfig.setTimestamp(DateUtility.getCurrentTimeStamp());
        return tenantService.saveSchoolTenantDBConfig(schoolTenantDBConfig);
    }

    // NOT IN USE
    @GetMapping("/get-tenant-dbconfig/{tenantCode}")
    public SchoolTenantDBConfig getSchoolTenantConfig(@PathVariable String tenantCode) {
        return tenantService.getSchoolTenantDBConfig(tenantCode);
    }


}