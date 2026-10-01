package com.schooltech.sms.controller.communication;

import com.schooltech.sms.service.communication.EmailService;
import com.schooltech.sms.service.communication.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    private final EmailService emailService;
    private Logger logger = LoggerFactory.getLogger(EmailController.class);

    public EmailController(EmailService emailService, OtpService otpService) {
        this.emailService = emailService;
    }

    /*
    Method to send email directly. It is used in our website jit,jasminefiore
     */
    @PostMapping("/send-email")
    public ResponseEntity<String> submitEmail(@RequestParam String email, @RequestBody String message) {
        logger.info("submit-email :  email: {} ,body: {}", email, message);
        emailService.sendFormDataViaEmail(email, message);
        return ResponseEntity.ok("Email sent successfully on email " + email);
    }
}
