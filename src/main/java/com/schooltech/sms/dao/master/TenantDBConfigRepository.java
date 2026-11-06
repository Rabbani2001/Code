package com.schooltech.sms.dao.master;

import com.schooltech.sms.entity.master.SchoolTenantDBConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantDBConfigRepository extends JpaRepository<SchoolTenantDBConfig, String> {
    public Optional<SchoolTenantDBConfig> findByTenantId(String tenantId);


}
