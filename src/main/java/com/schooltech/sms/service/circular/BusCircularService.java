package com.schooltech.sms.service.circular;


import com.schooltech.sms.dao.client.circulars.BusCircularRepository;
import com.schooltech.sms.dao.client.payment.StudentBusFeeRepository;
import com.schooltech.sms.entity.client.circulars.BusCircular;
import com.schooltech.sms.entity.client.payment.StudentBusFee;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.exception.CircularNotFoundException;
import com.schooltech.sms.service.student.StudentFeeService;
import com.schooltech.sms.service.student.StudentService;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BusCircularService {
    @Autowired
    private BusCircularRepository busCircularRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentFeeService studentFeeService;
    @Autowired
    private StudentBusFeeRepository studentBusFeeRepository;


    public ResponseEntity<List<BusCircular>> getBusCirculars() {
        List<BusCircular> busCirculars = busCircularRepository.findAll();
        return new ResponseEntity<>(busCirculars, HttpStatus.OK);
    }

    public ResponseEntity<List<BusCircular>> getBusCircularsBySession(String session) {
        List<BusCircular> busCirculars = busCircularRepository.findBySession(session);
        return new ResponseEntity<>(busCirculars, HttpStatus.OK);
    }

    public ResponseEntity<BusCircular> getBusCircularsByClassName(String route) {
        Optional<BusCircular> busCircularOptional = busCircularRepository.findByBusRoute(route);
        BusCircular busCircular = busCircularOptional.orElseThrow(() -> new CircularNotFoundException("Bus Circular Not Found"));
        return new ResponseEntity<>(busCircular, HttpStatus.OK);
    }

    public ResponseEntity<String> saveBusCirculars(List<BusCircular> busCirculars) {
        busCirculars.forEach(busCircular -> {
            Optional<BusCircular> busCircularOptional =
                    busCircularRepository.findByBusRouteAndSession(busCircular.getBusRoute(), busCircular.getSession());
            busCircularOptional.ifPresentOrElse(existingCircular -> {
                throw new RuntimeException("Bus Circular: " + busCircular.getBusRoute() + ":" + busCircular.getSession() + " already Present. Aborting Save");
            }, () -> {
                busCircular.setTimestamp(DateUtility.getCurrentTimeStamp());
                busCircularRepository.save(busCircular);
            });
        });
        return ResponseEntity.ok("Bus Circular Data Saved");
    }

    public ResponseEntity<String> updateBusCirculars(List<BusCircular> busCirculars) {
        busCirculars.forEach(busCircular -> {
            Optional<BusCircular> busCircularOptional =
                    busCircularRepository.findByBusRouteAndSession(busCircular.getBusRoute(), busCircular.getSession());
            busCircularOptional.ifPresentOrElse(existingCircular -> {
                updateNonNullFields(existingCircular, busCircular);
                existingCircular.setTimestamp(DateUtility.getCurrentTimeStamp());
                busCircularRepository.save(existingCircular);
            }, () -> {
                throw new CircularNotFoundException("Bus Circular: " + busCircular.getBusRoute() + ":" + busCircular.getSession() + " not present. Aborting Update");
            });
        });
        return ResponseEntity.ok("Bus Circular Data Updated");
    }

    private void updateNonNullFields(BusCircular existingCircular, BusCircular newCircular) {
//        if (newCircular.getMonthlyFee() != null) existingCircular.setMonthlyFee(newCircular.getMonthlyFee());
//        if (newCircular.getLateFee() != null) existingCircular.setLateFee(newCircular.getLateFee());
//        if (newCircular.getLateFeeStartDate() != null)
//            existingCircular.setLateFeeStartDate(newCircular.getLateFeeStartDate());
        if (newCircular.getBusMonthlyFees() != null)
            existingCircular.setBusMonthlyFees(newCircular.getBusMonthlyFees());
        if (newCircular.getBusFeesRules() != null)
            existingCircular.setBusFeesRules(newCircular.getBusFeesRules());

    }

    public ResponseEntity<String> updateAssociatedFees(List<BusCircular> busCirculars) {
        busCirculars.forEach(busCircular -> {
            Optional<BusCircular> busCircularOptional =
                    busCircularRepository.findByBusRouteAndSession(busCircular.getBusRoute(), busCircular.getSession());
            busCircularOptional.ifPresentOrElse(existingBusCircular -> {
                String busRoute = existingBusCircular.getBusRoute();
                List<Student> studentList = studentService.getStudentsByBusRoute(busRoute).getBody();
                List<StudentBusFee> studentBusFeeList = new ArrayList<>();
                studentList.forEach(student -> {
                    StudentBusFee studentBusFeeDB = studentBusFeeRepository.findByStudentIdAndSession(student.getUsername(), existingBusCircular.getSession());
                    StudentBusFee studentBusFee = new StudentBusFee();
                    if (studentBusFeeDB != null) {
                        studentBusFee.setSession(busCircular.getSession());
                        studentBusFee.setUsername(student.getUsername());
                        studentFeeService.processStudentBusFeeDataForUpdate(studentBusFee, studentBusFeeDB, busCircular, studentBusFeeList);
                    }
                });
                studentBusFeeRepository.saveAll(studentBusFeeList);
            }, () -> {
                throw new CircularNotFoundException("Bus Circular: " + busCircular.getBusRoute() + ":" + busCircular.getSession() + " not present. Aborting Update associated bus fees");
            });
        });
        return ResponseEntity.ok("Class Circular Associated Fees Data Updated");
    }


    public ResponseEntity<String> deleteBusCirculars(String busRoute) {
        if (!busCircularRepository.existsByBusRoute(busRoute)) {
            throw new CircularNotFoundException("Bus Circular Not Available for Route: " + busRoute);
        }
        busCircularRepository.deleteByBusRoute(busRoute);
        return new ResponseEntity<>("Bus Circular has been deleted for Route: " + busRoute, HttpStatus.OK);
    }
}
