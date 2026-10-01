package com.schooltech.sms.service.communication;

import com.schooltech.sms.controller.communication.dto.Msg91OtpVerifyResponse;
import com.schooltech.sms.exception.InvalidOtpException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final SecureRandom random = new SecureRandom();
    private final SmsService smsService;
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public OtpService(SmsService smsService) {

        this.smsService = smsService;
    }

    /*
    Create otp  for user identification
     */
    public String generateOtp(String key) {
        String otp = String.valueOf(100000 + random.nextInt(900000)); // Generate 6-digit OTP
        otpStorage.put(key, otp);
        return otp;
    }

    /*
     Validate otp  for user identification
    */
    public boolean validateEmailOtp(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }

    public boolean validateSmsOtp(String phoneNo, String otp) {

        Msg91OtpVerifyResponse verifyResponse =
                smsService.verifyOtp(phoneNo, otp);
        if ("success".equalsIgnoreCase(verifyResponse.getType())) {
            return true;
        } else if ("error".equalsIgnoreCase(verifyResponse.getType())) {
            throw new InvalidOtpException(verifyResponse.getMessage());
            //return false;
        }
        return false;
    }

    /*
    Clear otp  for user identification
    */
    public void clearOtp(String key) {
        otpStorage.remove(key);
    }
}

