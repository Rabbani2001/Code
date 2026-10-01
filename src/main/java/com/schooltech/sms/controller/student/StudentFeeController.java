package com.schooltech.sms.controller.student;

import com.schooltech.sms.controller.student.dto.*;
import com.schooltech.sms.entity.client.payment.*;
import com.schooltech.sms.service.student.StudentFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class StudentFeeController {
    @Autowired
    private StudentFeeService studentFeeService;

    ///////////////////// STUDENT FEE///////////////////////////////
    @GetMapping("/getStudentFees/{studentId}")
    public ResponseEntity<StudentFee> getStudentFees(@PathVariable String studentId, @RequestParam String session) {
        return studentFeeService.getStudentFee(studentId, session);
    }

    @GetMapping("/getStudentFees")
    public ResponseEntity<List<StudentFee>> getStudentFees() {
        return studentFeeService.getStudentFees();
    }

    @PostMapping("/saveStudentFees")
    @Transactional
    public ResponseEntity<String> saveStudentFees(@RequestBody List<StudentFeeDTO> studentFeesDTO) {
        return studentFeeService.saveStudentFees(studentFeesDTO);
    }

    @PutMapping("/updateStudentFees")
    @Transactional
    public ResponseEntity<String> updateStudentFees(@RequestBody List<StudentFeeDTO> studentFeesDTO) {
        return studentFeeService.updateStudentFees(studentFeesDTO);
    }

    @PutMapping("/updateStudentFeesCombined")
    @Transactional
    public ResponseEntity<String> updateStudentFeesCombined(@RequestBody StudentFeeCombinedDTO studentFeesCombinedDTO) {
        return studentFeeService.updateStudentFeesCombined(studentFeesCombinedDTO);
    }

    ///////////////////// STUDENT PAYMENT ///////////////////////////////

    @GetMapping("/getSchoolFeeReceipts/{studentId}")
    public ResponseEntity<List<FeeReceiptsDTO>> getPaymentsByStudentId(@PathVariable String studentId, @RequestParam String session) {
        return studentFeeService.getSchoolFeeReceiptsByStudentId(studentId, session);
    }

    @GetMapping("/getSchoolFeeReceipts")
    public ResponseEntity<List<StudentFeeReceipt>> getPayments() {
        return studentFeeService.getSchoolFeeReceipts();
    }

    @GetMapping("/getSchoolFeeReceiptsBetweenDates/{session}")
    public ResponseEntity<List<FeeReceiptsDTO>> getSchoolFeeReceiptsBetweenDates(@RequestParam LocalDate startDate, LocalDate endDate, @PathVariable String session) {
        return studentFeeService.getSchoolFeeReceiptsBetweenDates(startDate, endDate, session);
    }

    @PostMapping("/saveSchoolFeeReceipts")
    @Transactional
    public ResponseEntity<String> saveSchoolFeeReceipts(@RequestBody List<StudentFeeReceipt> studentFeeReceipts) {
        return studentFeeService.saveSchoolFeeReceipts(studentFeeReceipts);
    }

    @DeleteMapping("/deleteStudentFeeReceipt/{receiptNo}")
    @Transactional
    public ResponseEntity<String> deleteStudentFeeReceipt(
            @PathVariable String receiptNo,
            @RequestBody DeleteStudentFeeReceiptRequestDTO request) {

        studentFeeService.deleteStudentFeeReceipt(receiptNo, request.getUsername(), request.getSession(), request.getDeletedBy());
        return ResponseEntity.ok("Receipt " + receiptNo + " deleted successfully.");
    }

    ///////////////////// STUDENT FEE STATS ///////////////////////////////

    @GetMapping("/getStudentFeeStats")
    public ResponseEntity<StudentFeeStats> getStudentFeeStats(@RequestParam String session) {
        return studentFeeService.getStudentFeeStats(session);
    }

    @GetMapping("/getStudentCombineFeeStats")
    public ResponseEntity<StudentCombineFeeStats> getStudentCombineFeeStats(@RequestParam String session) {
        return studentFeeService.getStudentCombineFeeStats(session);
    }
    ///////////////////// STUDENT BUS FEE///////////////////////////////

    @GetMapping("/getStudentBusFees/{studentId}")
    public ResponseEntity<StudentBusFee> getStudentBusFees(@PathVariable String studentId, @RequestParam String session) {
        return studentFeeService.getStudentBusFee(studentId, session);
    }

    @GetMapping("/getStudentBusFees")
    public ResponseEntity<List<StudentBusFee>> getStudentBusFees() {
        return studentFeeService.getStudentBusFees();
    }

    @PostMapping("/saveStudentBusFees")
    @Transactional
    public ResponseEntity<String> saveStudentBusFees(@RequestBody List<StudentBusFeeDTO> studentBusFeesDTO) {
        return studentFeeService.saveStudentBusFees(studentBusFeesDTO);
    }

    @DeleteMapping("/deleteStudentBusFeeReceipt/{receiptNo}")
    @Transactional
    public ResponseEntity<String> deleteStudentBusFeeReceipt(
            @PathVariable String receiptNo,
            @RequestBody DeleteStudentFeeReceiptRequestDTO request) {

        studentFeeService.deleteStudentBusFeeReceipt(receiptNo, request.getUsername(), request.getSession(), request.getDeletedBy());
        return ResponseEntity.ok("Bus Fee Receipt " + receiptNo + " deleted successfully.");
    }

    @PutMapping("/updateStudentBusFees")
    @Transactional
    public ResponseEntity<String> updateStudentBusFees(@RequestBody List<StudentBusFeeDTO> studentBusFeesDTO) {
        return studentFeeService.updateStudentBusFees(studentBusFeesDTO);
    }

    ///////////////////// STUDENT BUS PAYMENT ///////////////////////////////

    @GetMapping("/getBusFeeReceipts/{studentId}")
    public ResponseEntity<List<BusFeeReceiptsDTO>> getBusFeeReceiptsByStudentId(@PathVariable String studentId, @RequestParam String session) {
        return studentFeeService.getBusFeeReceiptsByStudentId(studentId, session);
    }

    @GetMapping("/getBusFeeReceipts")
    public ResponseEntity<List<StudentBusFeeReceipt>> getBusFeeReceipts() {
        return studentFeeService.getBusFeeReceipts();
    }

    @PostMapping("/saveBusFeeReceipts")
    @Transactional
    public ResponseEntity<String> saveBusFeeReceipts(@RequestBody List<StudentBusFeeReceipt> StudentBusFeeReceipts) {
        return studentFeeService.saveBusFeeReceipts(StudentBusFeeReceipts);
    }
}
