package com.schooltech.sms.controller.payment.leaves;

import com.schooltech.sms.entity.client.payment.leaves.EmployeeLeaves;
import com.schooltech.sms.service.payment.leaves.EmployeeLeavesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeLeavesController {
    @Autowired
    private EmployeeLeavesService employeeLeavesService;

    @GetMapping("/getEmployeeLeavesBySession")
    public ResponseEntity<List<EmployeeLeaves>> getEmployeeLeavesBySession(@RequestParam String session) {
        return employeeLeavesService.getEmployeeLeavesBySession(session);
    }

    @GetMapping("/getEmployeeLeavesByUsername/{username}")
    public ResponseEntity<EmployeeLeaves> getEmployeeLeavesByUsername(@PathVariable String username, @RequestParam String session) {
        return employeeLeavesService.getEmployeeLeavesByUsername(username, session);
    }

    @PostMapping("/saveEmployeeLeaves")
    @Transactional
    public ResponseEntity<String> saveEmployeeLeaves(@RequestBody EmployeeLeaves employeeLeaves) {
        return employeeLeavesService.saveEmployeeLeaves(employeeLeaves);
    }

//    @PutMapping("/updateEmployeeLeaves")
//    //@Transactional
//    public ResponseEntity<String> updateEmployeeSalaries(@RequestBody List<EmployeeLeaves> employeeSalaries) {
//        return employeeLeavesService.updateEmployeeSalaries(employeeSalaries);
//    }

}
