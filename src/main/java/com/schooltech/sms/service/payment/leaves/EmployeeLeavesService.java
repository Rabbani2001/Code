package com.schooltech.sms.service.payment.leaves;


import com.schooltech.sms.dao.client.payment.leaves.EmployeeLeavesRepository;
import com.schooltech.sms.entity.client.payment.leaves.EmployeeLeaves;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeLeavesService {
    @Autowired
    private EmployeeLeavesRepository employeeLeavesRepository;

    public ResponseEntity<List<EmployeeLeaves>> getEmployeeLeavesBySession(String session) {
        List<EmployeeLeaves> employeeSalaries = employeeLeavesRepository.findBySession(session);
        return new ResponseEntity<>(employeeSalaries, HttpStatus.OK);
    }

    public ResponseEntity<EmployeeLeaves> getEmployeeLeavesByUsername(String username, String session) {
        Optional<EmployeeLeaves> employeeLeaves = employeeLeavesRepository.findByUsernameAndSession(username, session);
        EmployeeLeaves employeeLeaves1 = employeeLeaves.orElseThrow(() -> new RuntimeException("Employee Leaves Not Availble Not Available"));
        return new ResponseEntity<>(employeeLeaves1, HttpStatus.OK);
    }

    public ResponseEntity<String> saveEmployeeLeaves(EmployeeLeaves employeeLeaves) {
        Optional<EmployeeLeaves> employeeLeavesOptional =
                employeeLeavesRepository.findByUsernameAndSession(employeeLeaves.getUsername(), employeeLeaves.getSession());
        employeeLeavesOptional.ifPresentOrElse(existingLeaves -> {
            updateNonNullFields(existingLeaves, employeeLeaves);
            existingLeaves.setTimestamp(DateUtility.getCurrentTimeStamp());
            employeeLeavesRepository.save(existingLeaves);
        }, () -> {
            employeeLeaves.setTimestamp(DateUtility.getCurrentTimeStamp());
            employeeLeavesRepository.save(employeeLeaves);
        });
        return ResponseEntity.ok("Employee Leaves Data Saved");
    }

    private void updateNonNullFields(EmployeeLeaves employeeLeaves, EmployeeLeaves newLeaves) {
        if (newLeaves.getPaidLeaves() != null)
            employeeLeaves.setPaidLeaves(newLeaves.getPaidLeaves());
        if (newLeaves.getSickLeaves() != null)
            employeeLeaves.setSickLeaves(newLeaves.getSickLeaves());
        if (newLeaves.getCasualLeaves() != null)
            employeeLeaves.setCasualLeaves(newLeaves.getCasualLeaves());
        if (newLeaves.getLeavesApplied() != null)
            employeeLeaves.setLeavesApplied(newLeaves.getLeavesApplied());
        if (newLeaves.getLeavesTaken() != null)
            employeeLeaves.setLeavesTaken(newLeaves.getLeavesTaken());

    }

}
