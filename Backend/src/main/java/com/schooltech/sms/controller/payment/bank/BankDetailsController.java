package com.schooltech.sms.controller.payment.bank;


import com.schooltech.sms.entity.client.payment.bank.BankDetails;
import com.schooltech.sms.service.payment.bank.BankDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BankDetailsController {

    private final BankDetailsService bankDetailsService;

    public BankDetailsController(BankDetailsService bankDetailsService) {
        this.bankDetailsService = bankDetailsService;
    }

    // GET
    @GetMapping("/getBankDetails/{username}")
    public BankDetails getBankDetails(@PathVariable String username) {
        return bankDetailsService.getBankDetails(username);
    }

    @GetMapping("/isBankAccountNoExist/{bankAccountNo}")
    public ResponseEntity<Boolean> isBankAccountNoExist(@PathVariable String bankAccountNo) {
        return ResponseEntity.ok(bankDetailsService.isBankAccountNoExist(bankAccountNo));
    }

    @GetMapping("/isUpiAlreadyExist/{upiId}")
    public ResponseEntity<Boolean> isUpiAlreadyExist(@PathVariable String upiId) {
        return ResponseEntity.ok(bankDetailsService.isUpiAlreadyExist(upiId));
    }

    // SAVE
    @PostMapping("/saveBankDetails")
    public ResponseEntity<String> saveBankDetails(@Valid @RequestBody BankDetails bankDetails) {
        return bankDetailsService.saveBankDetails(bankDetails);
    }

    // UPDATE
//    @PutMapping("/updateBankDetails")
//    public ResponseEntity<String> updateBankDetails(@RequestBody BankDetails bankDetails) {
//        return bankDetailsService.updateBankDetails(bankDetails);
//    }
}
