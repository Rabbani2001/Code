package com.schooltech.sms.dao.client.payment;

import com.schooltech.sms.entity.client.payment.StudentBusFeeReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentBusFeeReceiptRepository extends JpaRepository<StudentBusFeeReceipt, Long> {
//    @Query("SELECT e FROM StudentBusFeeReceipt e WHERE e.username= :studentId AND e.session= :session")
//    List<StudentBusFeeReceipt> findByStudentIdAndSession(@Param("studentId") String studentId, @Param("session") String session);

    List<StudentBusFeeReceipt> findByUsernameAndSessionOrderByReceiptNoDesc(String studentId, String session);

    List<StudentBusFeeReceipt> findByUsernameAndSessionOrderByReceiptNoAsc(String studentId, String session);

    Optional<StudentBusFeeReceipt> findByReceiptNo(String receiptNo);
}
