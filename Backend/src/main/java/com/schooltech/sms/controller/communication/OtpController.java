package com.schooltech.sms.controller.communication;

import com.schooltech.sms.controller.communication.dto.Msg91OtpVerifyResponse;
import com.schooltech.sms.controller.communication.dto.RecipientDTO;
import com.schooltech.sms.exception.InvalidOtpException;
import com.schooltech.sms.service.communication.EmailService;
import com.schooltech.sms.service.communication.OtpService;
import com.schooltech.sms.service.communication.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OtpController {

    private final EmailService emailService;
    private final OtpService otpService;
    private final SmsService smsService;

    public OtpController(EmailService emailService, OtpService otpService, SmsService smsService) {
        this.emailService = emailService;
        this.otpService = otpService;
        this.smsService = smsService;
    }

    // ---------------- EMAIL OTP ----------------
    @PostMapping("/send-otp-via-email")
    public ResponseEntity<String> generateOtp(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        emailService.sendOtpOnEmail(email, otp);
        return ResponseEntity.ok("Email OTP sent successfully on email " + email);
    }

//    @PostMapping("/send-credentials")
//    public ResponseEntity<String> sendCred(@RequestParam String email, String user) {
//        String otp = otpService.generateOtp(email);
//        emailService.sendCredentialsEmail(email, user);
//        return ResponseEntity.ok("Credential sent successfully on email " + email);
//    }

    /*
    This is only used as of now from Postman. Can be also used from UI
     */
    @PostMapping("/verify-email-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        if (otpService.validateEmailOtp(email, otp)) {
            otpService.clearOtp(email);
            return ResponseEntity.ok("Email OTP verified successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid Email OTP.");
        }
    }

    // ---------------- SMS OTP (NEW) ----------------
    @PostMapping("/send-otp-via-sms")
    public ResponseEntity<String> sendOtpViaSms(
            @RequestParam String phoneNo
    ) {
        smsService.sendOtp(phoneNo);
        return ResponseEntity.ok("OTP sent successfully on mobile" + phoneNo);
    }

    //Below api used for local testng of forgot password
    @PostMapping("/send-otp-via-sms-disabled")
    public ResponseEntity<String> sendOtpViaSmsDisabled(
            @RequestParam String phoneNo
    ) {
        //smsService.sendOtp(phoneNo);
        System.out.println("SCHOOLTECH... Otp not sent via SMS as it is Local environment");
        return ResponseEntity.ok("OTP sent successfully on mobile" + phoneNo);
    }

    @GetMapping("/verify-otp-via-sms")
    public ResponseEntity<String> verifyOtpViaSms(
            @RequestParam String phoneNo,
            @RequestParam String otp
    ) {
        Msg91OtpVerifyResponse verifyResponse =
                smsService.verifyOtp(phoneNo, otp);

        if ("success".equalsIgnoreCase(verifyResponse.getType())) {
            return ResponseEntity.ok("OTP verified successfully");
        } else if ("error".equalsIgnoreCase(verifyResponse.getType())) {
            throw new InvalidOtpException(verifyResponse.getMessage());
        }

        return ResponseEntity.ok("OTP verification failed");
    }

    // ---------------- SMS (NEW) ----------------

    @PostMapping("/send-msg-via-sms")
    public ResponseEntity<String> sendReminderViaSms(
            @RequestBody List<RecipientDTO> reciepientList
    ) {
        smsService.sendMessage(reciepientList);
        return ResponseEntity.ok("Messages sent successfully");
    }
}
