package com.schooltech.sms.dao.client.visitor;

import com.schooltech.sms.entity.client.visitor.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByVisitorId(String visitorId);

    List<Visitor> findByVisitType(String visitType);

    @Query("SELECT e FROM Visitor e WHERE e.date BETWEEN :startDate AND :endDate")
    List<Visitor> findAllVisitorBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}