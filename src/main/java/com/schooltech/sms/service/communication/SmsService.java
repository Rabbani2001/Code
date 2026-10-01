package com.schooltech.sms.service.communication;


import com.schooltech.sms.controller.communication.dto.Msg91OtpVerifyResponse;
import com.schooltech.sms.controller.communication.dto.Msg91SendOtpResponse;
import com.schooltech.sms.controller.communication.dto.Msg91SmsRequest;
import com.schooltech.sms.controller.communication.dto.RecipientDTO;
import com.schooltech.sms.exception.InvalidOtpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmsService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${msg91.auth-key}")
    private String authKey;
    @Value("${msg91.template-id}")
    private String templateId;
    @Value("${msg91.otp-url}")
    private String otpUrl;
    @Value("${msg91.otp-url-verify}")
    private String otpVerifyUrl;
    @Value("${msg91.otp-expiry-minutes}")
    private String otpExpiryMinutes;
    @Value("${msg91.real-time-response}")
    private String realTimeResponse;
    @Value("${msg91.sms-url}")
    private String smsUrl;
    @Value("${msg91.attendance-template-id}")
    private String attendanceTemplateId;


    /**
     * Send OTP using MSG91 DLT approved template
     */
    public void sendOtp(
            String phoneNo
    ) {

        // Build URL with query params
        String url = UriComponentsBuilder
                .fromHttpUrl(otpUrl)
                .queryParam("mobile", phoneNo)
                .queryParam("authkey", authKey)
                .queryParam("otp_expiry", otpExpiryMinutes)
                .queryParam("template_id", templateId)
                .queryParam("realTimeResponse", realTimeResponse)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authkey", authKey);   // 🔥 IMPORTANT

        // Optional params for template variables
        Map<String, String> requestBody = Map.of(
                "Param1", "value1",
                "Param2", "value2",
                "Param3", "value3"
        );

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Msg91SendOtpResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, Msg91SendOtpResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || "error".equalsIgnoreCase(response.getBody().getType())) {

            throw new InvalidOtpException(
                    response.getBody() != null
                            ? response.getBody().getType()
                            : "Failed to send OTP via MSG91"
            );
        }
    }

    public Msg91OtpVerifyResponse verifyOtp(
            String phoneNo,
            String otp
    ) {
        // Build URL with query params
        String url = UriComponentsBuilder
                .fromHttpUrl(otpVerifyUrl)
                .queryParam("otp", otp)
                .queryParam("mobile", phoneNo)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("authkey", authKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Msg91OtpVerifyResponse> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, Msg91OtpVerifyResponse.class);

        return response.getBody();
    }

    public void sendMessage(
            List<RecipientDTO> recipientList
    ) {
        if (recipientList.isEmpty()) {
            throw new RuntimeException("MSG91 Send Message via SMS failed as recipient List is empty ");
        }

        // Build URL with query params
        String url = UriComponentsBuilder
                .fromHttpUrl(smsUrl)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authkey", authKey);   // 🔥 IMPORTANT

        List<Map<String, String>> recipients = recipientList.stream()
                .map(recipient -> {

                    Map<String, String> map = new LinkedHashMap<>();

                    map.put("mobiles", recipient.getMobile());
                    map.put("StudentName", recipient.getStudentName());
                    map.put("SchoolName", recipient.getSchoolName());
                    map.put("Date", recipient.getDate());

                    return map;
                })
                .collect(Collectors.toList());

        Msg91SmsRequest request = Msg91SmsRequest.builder()
                .templateId(attendanceTemplateId)
                .shortUrl("0")
                .realTimeResponse(1)
                .recipients(recipients)
                .build();

        HttpEntity<Msg91SmsRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("MSG91 Send Message via SMS failed : " + response.getBody());
        }
    }
}
