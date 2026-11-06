package com.schooltech.sms.dao.client.sequence;

import com.schooltech.sms.entity.client.sequence.SessionSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionSequenceRepository extends JpaRepository<SessionSequence, Long> {

    Optional<SessionSequence> findByTenantCodeAndSessionKeyAndType(String tenantCode, String sessionKey, String type);
}