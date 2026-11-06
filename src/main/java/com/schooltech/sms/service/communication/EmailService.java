package com.schooltech.sms.service.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.controller.communication.dto.EmailBodyDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    @Autowired
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;


    @Value("${otp.expiration}")
    private int otpExpiration;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpOnEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Your OTP for Skoolish App");
            helper.setText("Your Skoolish App  OTP is: " + otp + ". It is valid for " + otpExpiration + " minutes.");
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    /*
     Method to send email when new account is created in Skoolish app. It is used in our erp skoolish app
     */
    public void sendCredentialsEmail(String toEmail, String user) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(toEmail);
//            helper.setSubject("Welcome to Skoolish App");
//            helper.setText("Your App credentials are:- \n LOGIN ID: " + user + " ,PASSWORD: good@123" + "\n\nPlease Note: Please update your password using ForgotPassword button on App Login Page");
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send Welcome email", e);
//        }
    }


    /*
     Method to send email directly. It is used in our website jit,jasminefiore
     */
    public void sendFormDataViaEmail(String to, String emailInfoJson) {
        try {
            EmailBodyDTO emailBodyDTO = objectMapper.readValue(emailInfoJson, EmailBodyDTO.class);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(emailBodyDTO.getSubject());
            helper.setText(emailBodyDTO.getEmailBody());
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email due to wrong email json string ", e);
        }
    }

}
