package com.schooltech.sms.dao.client.payment.leaves;


import com.schooltech.sms.entity.client.payment.leaves.EmployeeLeaves;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeLeavesRepository extends JpaRepository<EmployeeLeaves, Long> {

    List<EmployeeLeaves> findBySession(String session);

    @Query("SELECT e FROM EmployeeLeaves e WHERE e.username= :username AND e.session= :session")
    Optional<EmployeeLeaves> findByUsernameAndSession(@Param("username") String username, @Param("session") String session);
}
