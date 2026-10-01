package com.schooltech.sms.service.facerecognition;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FaceRecognitionService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String identifyFace(String base64Image) {

        String url = "http://127.0.0.1:8000/identify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("image", base64Image);

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                url,
                request,
                Map.class
        );

        if (response.getBody() != null) {
            return (String) response.getBody().get("teacher");
        }

        return null;
    }
}