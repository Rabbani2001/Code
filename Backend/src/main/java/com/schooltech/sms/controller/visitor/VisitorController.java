package com.schooltech.sms.controller.visitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.visitor.dto.VisitorDTO;
import com.schooltech.sms.entity.client.visitor.Visitor;
import com.schooltech.sms.service.file.FileUploadS3Service;
import com.schooltech.sms.service.visitor.VisitorService;
import com.schooltech.sms.utility.DateUtility;
import com.schooltech.sms.utility.SessionSequenceUtility;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class VisitorController {

    @Autowired
    private VisitorService visitorService;
    @Autowired
    private SessionSequenceUtility sessionSequenceUtility;
    @Autowired
    private FileUploadS3Service fileUploadS3Service;
    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping("/saveVisitor")
    public ResponseEntity<String> saveVisitor(
            @RequestParam("visitorParams") String visitorParams,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "photo1", required = false) MultipartFile photo1,
            @RequestParam(value = "photo2", required = false) MultipartFile photo2,
            @RequestParam(value = "photo3", required = false) MultipartFile photo3,
            @RequestParam(value = "photo4", required = false) MultipartFile photo4
    ) throws IOException {
        VisitorDTO visitorDTO = objectMapper.readValue(visitorParams, VisitorDTO.class);

        String visitorId = sessionSequenceUtility.generateSessionSequence(AppConstant.VISTOR_TYPE_SEQUENCE);
        Visitor visitor = visitorDTO.getVisitor();
        visitor.setVisitorId(visitorId);
        visitor.setTimestamp(DateUtility.getCurrentTimeStamp());

        String basePath = visitorDTO.getTenantId() + "/docs/visitor/" + visitorId + "/";
        Map<String, String> documents = new HashMap<>();

        // profile photo — required, same as before
        String profileUrl = fileUploadS3Service.uploadFile(file, basePath);
        documents.put(AppConstant.PROFILE_PHOTO, profileUrl);

        // additional photos — optional
        uploadIfPresent(photo1, basePath, "photo1", documents);
        uploadIfPresent(photo2, basePath, "photo2", documents);
        uploadIfPresent(photo3, basePath, "photo3", documents);
        uploadIfPresent(photo4, basePath, "photo4", documents);

        visitor.setDocuments(documents);

        Visitor savedVisitor = visitorService.saveVisitor(visitor);
        return ResponseEntity.ok("Visitor with id:" + savedVisitor.getVisitorId() + " saved successfully");
    }

    private void uploadIfPresent(MultipartFile photo, String basePath, String key, Map<String, String> documents) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            String url = fileUploadS3Service.uploadFile(photo, basePath);
            documents.put(key, url);
        }
    }

    @PutMapping("/updateVisitor")
    @Transactional
    public ResponseEntity<String> updateVisitor(@RequestBody Visitor visitor) {
        return visitorService.updateVisitor(visitor);
    }

    @GetMapping("/getVisitors")
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        List<Visitor> visitors = visitorService.getAllVisitors();
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/getVisitors/{id}")
    public ResponseEntity<Visitor> getVisitorById(@PathVariable String visitorId) {
        Visitor visitor = visitorService.getVisitorByVisitorId(visitorId);
        return ResponseEntity.ok(visitor);
    }

    @GetMapping("/getVisitorsBetweenDates")
    public ResponseEntity<List<Visitor>> getVisitorsBetweenDates(@RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return visitorService.findAllVisitorBetweenDates(startDate, endDate);
    }

}