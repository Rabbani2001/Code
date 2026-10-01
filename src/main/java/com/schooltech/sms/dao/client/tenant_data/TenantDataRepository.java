package com.schooltech.sms.dao.client.tenant_data;

import com.schooltech.sms.entity.client.tenant_data.TenantData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantDataRepository extends JpaRepository<TenantData, Long> {
    Optional<TenantData> findFirstBy();
}