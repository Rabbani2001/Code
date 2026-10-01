package com.schooltech.security.jwt.service;


import com.schooltech.sms.dao.master.TenantDBConfigRepository;
import com.schooltech.sms.dao.master.TenantRepository;
import com.schooltech.sms.entity.master.SchoolTenant;
import com.schooltech.sms.entity.master.SchoolTenantDBConfig;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantDBConfigRepository tenantDBConfigRepository;

    public ResponseEntity<String> saveSchoolTenant(SchoolTenant schoolTenant) {
        try {
            Optional<SchoolTenant> schoolTenant1 = tenantRepository.findByTenantCode(schoolTenant.getTenantCode());
            schoolTenant1.ifPresent(schoolTenantDB -> schoolTenant.setId(schoolTenantDB.getId()));
            schoolTenant.setTimestamp(DateUtility.getCurrentTimeStamp());
            tenantRepository.save(schoolTenant);
            return ResponseEntity.ok("School-Tenant Saved");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to Save School Tenant");
        }
    }

    public ResponseEntity<String> updateSchoolTenant(SchoolTenant schoolTenant) {
        tenantRepository.findByTenantCode(schoolTenant.getTenantCode()).ifPresentOrElse(existingTenant -> {
            updateNonNullFields(existingTenant, schoolTenant);
            existingTenant.setTimestamp(DateUtility.getCurrentTimeStamp());
            tenantRepository.save(existingTenant);
        }, () -> {
            throw new RuntimeException("Tenant: " + schoolTenant.getTenantCode() + " not present. Aborting Update");
        });
        return ResponseEntity.ok("Tenant Data Updated");
    }

    public ResponseEntity<String> updateSchoolTenantByTenantId(SchoolTenant schoolTenant) {
        tenantRepository.findByTenantId(schoolTenant.getTenantId()).ifPresentOrElse(existingTenant -> {
            updateNonNullFields(existingTenant, schoolTenant);
            existingTenant.setTimestamp(DateUtility.getCurrentTimeStamp());
            tenantRepository.save(existingTenant);
        }, () -> {
            throw new RuntimeException("Tenant: " + schoolTenant.getTenantCode() + " not present. Aborting Update");
        });
        return ResponseEntity.ok("Tenant Data Updated");
    }

    private void updateNonNullFields(SchoolTenant existingSchoolTenant, SchoolTenant newSchoolTenant) {
        if (newSchoolTenant.getTenantName() != null)
            existingSchoolTenant.setTenantName(newSchoolTenant.getTenantName());
        if (newSchoolTenant.getEmail() != null) existingSchoolTenant.setEmail(newSchoolTenant.getEmail());
        if (newSchoolTenant.getPhoneNo() != null) existingSchoolTenant.setPhoneNo(newSchoolTenant.getPhoneNo());
        if (newSchoolTenant.getAltPhoneNo() != null)
            existingSchoolTenant.setAltPhoneNo(newSchoolTenant.getAltPhoneNo());
        if (newSchoolTenant.getIsActive() != null)
            existingSchoolTenant.setIsActive(newSchoolTenant.getIsActive());
        if (newSchoolTenant.getTenantInfo() != null)
            existingSchoolTenant.setTenantInfo(newSchoolTenant.getTenantInfo());
        if (newSchoolTenant.getAttendanceControl() != null)
            existingSchoolTenant.setAttendanceControl(newSchoolTenant.getAttendanceControl());
    }

    public SchoolTenant getSchoolTenant(String tenantCode) {
        Optional<SchoolTenant> schoolTenant1 = tenantRepository.findByTenantCode(tenantCode);
        return schoolTenant1.orElse(null);
    }

    public SchoolTenant getSchoolTenantByTenantId(String tenantId) {
        Optional<SchoolTenant> schoolTenant1 = tenantRepository.findByTenantId(tenantId);
        return schoolTenant1.orElse(null);
    }

    public ResponseEntity<String> saveSchoolTenantDBConfig(SchoolTenantDBConfig schoolTenantDBConfig) {
        try {
            Optional<SchoolTenantDBConfig> schoolTenantDBConfig1 = tenantDBConfigRepository.findByTenantId(schoolTenantDBConfig.getTenantId());
            schoolTenantDBConfig1.ifPresent(schoolTenantDBConfigDB -> schoolTenantDBConfig.setId(schoolTenantDBConfigDB.getId()));
            tenantDBConfigRepository.save(schoolTenantDBConfig);
            return ResponseEntity.ok("School-Tenant DB Config Saved");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to Save School Tenant DB Config");
        }
    }

    public SchoolTenantDBConfig getSchoolTenantDBConfig(String tenantCode) {
        Optional<SchoolTenantDBConfig> schoolTenantDBConfig = tenantDBConfigRepository.findByTenantId(tenantCode);
        return schoolTenantDBConfig.orElse(null);
    }
}
