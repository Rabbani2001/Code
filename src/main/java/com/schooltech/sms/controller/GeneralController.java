package com.schooltech.sms.controller;


import com.schooltech.sms.service.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class GeneralController {
    @Autowired
    private GeneralService generalService;

    @GetMapping("/isEmployeeIdExists/{employeeId}")
    public ResponseEntity<Boolean> isTeacherEmployeeIdExists(@PathVariable String employeeId) {
        return generalService.isEmployeeIdExists(employeeId);
    }

    @GetMapping("/isPanNoAlreadyExists")
    public ResponseEntity<Boolean> isPanNoAlreadyExists(@RequestParam String panNo) {
        return ResponseEntity.ok(generalService.isPanNoAlreadyExists(panNo));
    }
}