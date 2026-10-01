package com.schooltech.sms.service.payment.salary;


import com.schooltech.sms.dao.client.payment.salary.EmployeeSalaryRepository;
import com.schooltech.sms.entity.client.payment.salary.EmployeeSalary;
import com.schooltech.sms.exception.CircularNotFoundException;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeSalaryService {
    @Autowired
    private EmployeeSalaryRepository employeeSalaryRepository;

    public ResponseEntity<List<EmployeeSalary>> getEmployeesSalaryBySession(String session) {
        List<EmployeeSalary> employeeSalaries = employeeSalaryRepository.findBySession(session);
        return new ResponseEntity<>(employeeSalaries, HttpStatus.OK);
    }

    public ResponseEntity<String> saveEmployeeSalary(List<EmployeeSalary> roleCirculars) {
        roleCirculars.forEach(employeeSalary -> {
            Optional<EmployeeSalary> employeeSalaryOptional =
                    employeeSalaryRepository.findByUsernameAndSession(employeeSalary.getUsername(), employeeSalary.getSession());
            employeeSalaryOptional.ifPresentOrElse(existingCircular -> {
                throw new RuntimeException("Employee Salary: " + employeeSalary.getUsername() + ":" + employeeSalary.getSession() + " already Present. Aborting Save");
            }, () -> {
                employeeSalary.setTimestamp(DateUtility.getCurrentTimeStamp());
                employeeSalaryRepository.save(employeeSalary);
            });
        });
        return ResponseEntity.ok("Employee Salary Data Saved");
    }

    public ResponseEntity<String> updateEmployeeSalaries(List<EmployeeSalary> employeeSalaries) {
        employeeSalaries.forEach(employeeSalary -> {
            Optional<EmployeeSalary> roleCircularOptional =
                    employeeSalaryRepository.findByUsernameAndSession(employeeSalary.getUsername(), employeeSalary.getSession());
            roleCircularOptional.ifPresentOrElse(existingSalary -> {
                updateNonNullFields(existingSalary, employeeSalary);
                existingSalary.setTimestamp(DateUtility.getCurrentTimeStamp());
                employeeSalaryRepository.save(existingSalary);
            }, () -> {
                throw new CircularNotFoundException("Employee Salary: " + employeeSalary.getUsername() + ":" + employeeSalary.getSession() + " not present. Aborting Update");
            });
        });
        return ResponseEntity.ok("Employee Salary Data Updated");
    }

    private void updateNonNullFields(EmployeeSalary employeeSalary, EmployeeSalary newEmployeeSalary) {

        if (newEmployeeSalary.getCtc() != null)
            employeeSalary.setCtc(newEmployeeSalary.getCtc());
        if (newEmployeeSalary.getBasicPay() != null)
            employeeSalary.setBasicPay(newEmployeeSalary.getBasicPay());
        if (newEmployeeSalary.getHra() != null) employeeSalary.setHra(newEmployeeSalary.getHra());
        if (newEmployeeSalary.getPf() != null)
            employeeSalary.setPf(newEmployeeSalary.getPf());
        if (newEmployeeSalary.getGratuity() != null)
            employeeSalary.setGratuity(newEmployeeSalary.getGratuity());
        if (newEmployeeSalary.getBonus() != null)
            employeeSalary.setBonus(newEmployeeSalary.getBonus());
        if (newEmployeeSalary.getTax() != null)
            employeeSalary.setTax(newEmployeeSalary.getTax());
        if (newEmployeeSalary.getArrear() != null)
            employeeSalary.setArrear(newEmployeeSalary.getArrear());
        if (newEmployeeSalary.getDeduction() != null)
            employeeSalary.setDeduction(newEmployeeSalary.getDeduction());

        if (newEmployeeSalary.getPayOut() != null)
            employeeSalary.setPayOut(newEmployeeSalary.getPayOut());

        if (newEmployeeSalary.getLwp() != null)
            employeeSalary.setLwp(newEmployeeSalary.getLwp());
    }

}
