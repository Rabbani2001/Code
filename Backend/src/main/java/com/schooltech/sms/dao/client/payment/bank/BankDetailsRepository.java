package com.schooltech.sms.dao.client.payment.bank;

import com.schooltech.sms.entity.client.payment.bank.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {

    Optional<BankDetails> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByBankAccountNo(String bankAccountNo);

    boolean existsByUpiId(String upiId);
}
