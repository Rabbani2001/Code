package com.schooltech.sms.service.facerecognition;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schooltech.sms.dao.client.attendence.FaceEmbeddingRepository;
import com.schooltech.sms.entity.client.attendence.FaceEmbedding;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;


@Service
public class FaceRecognitionService {

    private final RestTemplate restTemplate;
    private final FaceEmbeddingRepository faceEmbeddingRepository;
    private final ObjectMapper objectMapper;

    public FaceRecognitionService(FaceEmbeddingRepository faceEmbeddingRepository, ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.faceEmbeddingRepository = faceEmbeddingRepository;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> identifyFace(MultipartFile image) {

        String url = "http://face-api:8000/recognize-face";

        try {
            // 1. Get all saved embeddings from database
            List<FaceEmbedding> savedFaces = faceEmbeddingRepository.findAll();

            if (savedFaces.isEmpty()) {throw new RuntimeException("No face embeddings found in database");}


            // 2. Prepare embeddings for Python
            List<Map<String, Object>> savedEmbeddings = new ArrayList<>();

            for (FaceEmbedding face : savedFaces) {

                Map<String, Object> faceData = new HashMap<>();
                faceData.put("username", face.getUsername());

                // Convert embedding JSON String
                // back to Java List<Double>
                List<Double> embedding = objectMapper.readValue(face.getEmbedding(), new TypeReference<List<Double>>() {});


                faceData.put("embedding", embedding);

                savedEmbeddings.add(faceData);
            }

            // 3. Convert Java List to JSON String
            String savedEmbeddingsJson = objectMapper.writeValueAsString(savedEmbeddings);

            // 4. Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();


            // 5. Prepare image
            ByteArrayResource imageResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };

            // 6. Add image
            body.add("image", imageResource);

            // 7. Add embeddings
            body.add("saved_embeddings", savedEmbeddingsJson);


            // 8. Create request
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            // 9. Call Python
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);


            return response.getBody();


        } catch (Exception e) {

            throw new RuntimeException("Face recognition failed: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> saveOrUpdateEmbedding(String username, MultipartFile image) {

        String url = "http://face-api:8000/generate-embedding";

        try {

            // -------------------------
            // 1. Prepare multipart body
            // -------------------------
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();


            ByteArrayResource imageResource = new ByteArrayResource(image.getBytes()) {

                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };


            body.add("image", imageResource);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);


            // -------------------------
            // 2. Call Python
            // -------------------------
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);


            Map<String, Object> pythonResponse = response.getBody();

            if (pythonResponse == null ||
                    pythonResponse.get("embedding") == null) {

                throw new RuntimeException("Embedding not returned from Python");
            }

            // -------------------------
            // 3. Get embedding
            // -------------------------

            List<Double> embedding = (List<Double>) pythonResponse.get("embedding");

            // -------------------------
            // 4. Convert List → JSON
            // -------------------------
            String embeddingJson = objectMapper.writeValueAsString(embedding);

            // -------------------------
            // 5. Check existing user
            // -------------------------
            Optional<FaceEmbedding> existingFace = faceEmbeddingRepository.findByUsername(username);

            FaceEmbedding faceEmbedding;

            String action;

            if (existingFace.isPresent()) {

                // UPDATE
                faceEmbedding = existingFace.get();

                faceEmbedding.setEmbedding(embeddingJson);

                faceEmbedding.setTimestamp(DateUtility.getCurrentTimeStamp());

                action = "updated";

            } else {

                // INSERT

                faceEmbedding = new FaceEmbedding();

                faceEmbedding.setUsername(username);

                faceEmbedding.setEmbedding(embeddingJson);

                faceEmbedding.setTimestamp(DateUtility.getCurrentTimeStamp());

                action = "created";
            }

            // -------------------------
            // 6. Save to MySQL
            // -------------------------

            FaceEmbedding saved = faceEmbeddingRepository.save(faceEmbedding);
            // -------------------------
            // 7. Return response
            // -------------------------

            Map<String, Object> result = new HashMap<>();

            result.put("message", "Embedding " + action + " successfully");

            result.put("username", saved.getUsername());
            result.put("embeddingId", saved.getId());
            result.put("dimensions", embedding.size());

            return result;


        } catch (Exception e) {

            throw new RuntimeException("Embedding generation failed: " + e.getMessage(), e);
        }
    }
}