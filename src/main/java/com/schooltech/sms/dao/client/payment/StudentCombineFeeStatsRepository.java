package com.schooltech.sms.dao.client.payment;


import com.schooltech.sms.entity.client.payment.StudentCombineFeeStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentCombineFeeStatsRepository extends JpaRepository<StudentCombineFeeStats, Long> {

    Optional<StudentCombineFeeStats> findBySession(String session);
    
}
