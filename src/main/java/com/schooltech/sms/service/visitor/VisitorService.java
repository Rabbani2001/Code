package com.schooltech.sms.service.visitor;

import com.schooltech.sms.dao.client.visitor.VisitorRepository;
import com.schooltech.sms.entity.client.visitor.Comment;
import com.schooltech.sms.entity.client.visitor.Visitor;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class VisitorService {

    @Autowired
    private VisitorRepository visitorRepository;

    public Visitor saveVisitor(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    public ResponseEntity<String> updateVisitor(Visitor visitor) {
        visitorRepository.findByVisitorId(visitor.getVisitorId()).ifPresentOrElse(existingVisitor -> {
            updateNonNullFields(existingVisitor, visitor);
            existingVisitor.setTimestamp(DateUtility.getCurrentTimeStamp());
            visitorRepository.save(existingVisitor);
        }, () -> {
            throw new RuntimeException("Visitor: " + visitor.getVisitorId() + " not present. Aborting Update");
        });
        return ResponseEntity.ok("Visitor Data Updated");
    }

    private void updateNonNullFields(Visitor existingVisitor, Visitor newVisitor) {
        if (newVisitor.getCheckOutTime() != null) existingVisitor.setCheckOutTime(newVisitor.getCheckOutTime());
        if (newVisitor.getVisitComments() != null) {
            Map<String, Comment> existingCommentsDB = existingVisitor.getVisitComments();
            existingCommentsDB.putAll(newVisitor.getVisitComments());
            existingVisitor.setVisitComments(existingCommentsDB);
        }
        if (newVisitor.getResolutionStatus() != null)
            existingVisitor.setResolutionStatus(newVisitor.getResolutionStatus());
        if (newVisitor.getVisitType() != null) existingVisitor.setVisitType(newVisitor.getVisitType());
        if (newVisitor.getPhoneNo() != null) existingVisitor.setPhoneNo(newVisitor.getPhoneNo());
        if (newVisitor.getEmail() != null) existingVisitor.setEmail(newVisitor.getEmail());
        if (newVisitor.getFullName() != null) existingVisitor.setFullName(newVisitor.getFullName());
        if (newVisitor.getAddress() != null) existingVisitor.setAddress(newVisitor.getAddress());

    }

    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    public Visitor getVisitorByVisitorId(String visitorId) {
        return visitorRepository.findByVisitorId(visitorId).orElseThrow(() -> new RuntimeException("Visitor not found with id: " + visitorId));
    }

    public ResponseEntity<List<Visitor>> findAllVisitorBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Visitor> visitorList = visitorRepository.findAllVisitorBetweenDates(startDate, endDate);
        return new ResponseEntity<>(visitorList, HttpStatus.OK);
    }
}