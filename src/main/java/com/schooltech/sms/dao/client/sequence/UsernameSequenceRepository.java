package com.schooltech.sms.dao.client.sequence;

import com.schooltech.sms.entity.client.sequence.UsernameSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsernameSequenceRepository
        extends JpaRepository<UsernameSequence, Long> {

    Optional<UsernameSequence> findByTenantCodeAndRole(String tenantCode, String role);
}