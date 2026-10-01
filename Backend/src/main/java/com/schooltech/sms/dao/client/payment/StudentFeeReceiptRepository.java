package com.schooltech.sms.dao.client.payment;


import com.schooltech.sms.entity.client.payment.StudentFeeReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentFeeReceiptRepository extends JpaRepository<StudentFeeReceipt, Long> {
//    @Query("SELECT e FROM StudentFeeReceipt e WHERE e.username= :studentId AND e.session= :session")
//    List<StudentFeeReceipt> findByStudentIdAndSession(@Param("studentId") String studentId, @Param("session") String session);

    List<StudentFeeReceipt> findByUsernameAndSessionOrderByReceiptNoDesc(String studentId, String session);

    //List<StudentFeeReceipt> findByDateAndSessionOrderByTimestampDesc(LocalDate date, String session);

    List<StudentFeeReceipt> findByDateBetweenAndSessionOrderByTimestampDesc(
            LocalDate startDate,
            LocalDate endDate,
            String session
    );

    Optional<StudentFeeReceipt> findByReceiptNo(String receiptNo);
}
