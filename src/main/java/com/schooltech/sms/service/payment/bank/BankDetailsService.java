package com.schooltech.sms.service.payment.bank;

import com.schooltech.sms.dao.client.payment.bank.BankDetailsRepository;
import com.schooltech.sms.entity.client.payment.bank.BankDetails;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BankDetailsService {

    private final BankDetailsRepository bankDetailsRepository;

    public BankDetailsService(BankDetailsRepository bankDetailsRepository) {
        this.bankDetailsRepository = bankDetailsRepository;
    }

    // GET
    public BankDetails getBankDetails(String username) {
        Optional<BankDetails> bankDetailsOptional = bankDetailsRepository.findByUsername(username);
        return bankDetailsOptional.orElse(null);
    }

    public boolean isBankAccountNoExist(String bankAccountNo) {
        return bankDetailsRepository.existsByBankAccountNo(bankAccountNo);
    }

    public boolean isUpiAlreadyExist(String upiId) {
        return bankDetailsRepository.existsByUpiId(upiId);
    }

    public ResponseEntity<String> saveBankDetails(BankDetails bankDetails) {
        bankDetailsRepository.findByUsername(bankDetails.getUsername()).ifPresentOrElse(existingBankDetails -> {
            updateNonNullFields(existingBankDetails, bankDetails);
            existingBankDetails.setTimestamp(DateUtility.getCurrentTimeStamp());
            bankDetailsRepository.save(existingBankDetails);
        }, () -> {
            bankDetails.setTimestamp(DateUtility.getCurrentTimeStamp());
            bankDetailsRepository.save(bankDetails);
        });
        return ResponseEntity.ok("Bank details saved successfully");
    }

    public void updateNonNullFields(BankDetails bankDetails, BankDetails incomingBankDetils) {
        if (incomingBankDetils.getBankName() != null) bankDetails.setBankName(incomingBankDetils.getBankName());
        if (incomingBankDetils.getBankAccountNo() != null)
            bankDetails.setBankAccountNo(incomingBankDetils.getBankAccountNo());
        if (incomingBankDetils.getAccountHolderName() != null)
            bankDetails.setAccountHolderName(incomingBankDetils.getAccountHolderName());
        if (incomingBankDetils.getIfscCode() != null) bankDetails.setIfscCode(incomingBankDetils.getIfscCode());
        if (incomingBankDetils.getUpiId() != null) bankDetails.setUpiId(incomingBankDetils.getUpiId());
    }
}
