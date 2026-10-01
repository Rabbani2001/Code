package com.schooltech.sms.controller.student;


import com.schooltech.sms.controller.student.dto.ParentDeactivationRequestDTO;
import com.schooltech.sms.controller.student.dto.ParentPanelDTO;
import com.schooltech.sms.entity.client.student.Parent;
import com.schooltech.sms.service.student.ParentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class ParentController {
    private static final Logger logger = LoggerFactory.getLogger(ParentController.class);
    @Autowired
    private ParentService parentService;

    @GetMapping("/getParents")
    public ResponseEntity<List<ParentPanelDTO>> getParents() {
        return parentService.getParents();
    }

    @GetMapping("/getDeActivatedParents")
    public ResponseEntity<List<ParentPanelDTO>> getDeActivatedParents() {
        return parentService.getDeActivatedParents();
    }


//    @GetMapping("/getParentByUsername/{username}")
//    public ResponseEntity<Parent> getParentByUsername(@PathVariable String username) {
//        Parent parent = parentService.getParentByUsername(username);
//        return new ResponseEntity<>(parent, HttpStatus.OK);
//    }

    @GetMapping("/getParentByUsername/{username}")
    public ResponseEntity<ParentProfileDTO> getParentByUsername(@PathVariable String username) {
        return new ResponseEntity<>(parentService.getParentProfileDTOByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/getParentByPhoneNo/{phoneNo}")
    public ResponseEntity<List<ParentPanelDTO>> getParentByPhoneNo(@PathVariable String phoneNo) {
        return ResponseEntity.ok(parentService.getParentByPhoneNo(phoneNo));
    }

    @GetMapping("/getParentsByName/{name}")
    public ResponseEntity<List<ParentPanelDTO>> getParentsByName(@PathVariable String name) {
        return ResponseEntity.ok(parentService.getParentsByName(name));
    }

    @GetMapping("/getAbandonedParents")
    public ResponseEntity<List<ParentPanelDTO>> getAbandonedParents() {
        return ResponseEntity.ok(parentService.getAbandonedParents());
    }

    @GetMapping("/isParentAadhaarExists/{aadharNo}")
    public ResponseEntity<Boolean> isStaffAadhaarExists(@PathVariable String aadharNo) {
        return ResponseEntity.ok(parentService.isParentAadhaarExists(aadharNo));
    }

//    @GetMapping("/getParentByEmail/{email}")
//    public ResponseEntity<Parent> getParentByEmail(@PathVariable String email) {
//        Parent parent = parentService.getParentByEmail(email);
//        return new ResponseEntity<>(parent, HttpStatus.OK);
//    }


//    @GetMapping("/getParentByPhoneNo/{phoneNo}")
//    public ResponseEntity<Parent> getParentByPhoneNo(@PathVariable String phoneNo) {
//        Parent parent = parentService.getParentByPhoneNo(phoneNo);
//        return new ResponseEntity<>(parent, HttpStatus.OK);
//    }

    @PostMapping("/saveParents")
    @Transactional
    public ResponseEntity<String> saveParents(@Valid @RequestBody List<@Valid Parent> parentList) {
        return parentService.saveParents(parentList);
    }

    @PutMapping("/updateParents")
    @Transactional
    public ResponseEntity<String> updateParents(@Valid @RequestBody List<@Valid Parent> parentList) {
        return parentService.updateParents(parentList);
    }

    @DeleteMapping("/deActivateParent/{username}")
    @Transactional
    public ResponseEntity<String> deActivateParentByUsername(
            @PathVariable String username,
            @RequestBody ParentDeactivationRequestDTO request) {

        parentService.deActivateParentByUsername(username, request.getUpdatedBy());
        return ResponseEntity.ok("Parent with username " + username + " has been deactivated successfully.");
    }


    @PutMapping("/activateParent/{username}")
    @Transactional
    public ResponseEntity<String> activateStaffByUsername(
            @PathVariable String username,
            @RequestBody ParentDeactivationRequestDTO request) {

        parentService.activateParentWithEntity(username, request.getUpdatedBy());
        return ResponseEntity.ok("Parent with username " + username + " has been activated successfully.");
    }

    @DeleteMapping("/deleteParent/{username}")
    @Transactional
    public ResponseEntity<String> deleteParentByUsername(@PathVariable String username) {
        parentService.deleteParentByUsername(username);
        return ResponseEntity.ok("Parent with username " + username + " has been deleted successfully.");
    }

}