package com.schooltech.sms.dao.client.payment;


import com.schooltech.sms.entity.client.payment.StudentBusFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentBusFeeRepository extends JpaRepository<StudentBusFee, Long> {
    @Query("SELECT e FROM StudentBusFee e WHERE e.username= :studentId AND e.session= :session")
    StudentBusFee findByStudentIdAndSession(@Param("studentId") String studentId, @Param("session") String session);
}
