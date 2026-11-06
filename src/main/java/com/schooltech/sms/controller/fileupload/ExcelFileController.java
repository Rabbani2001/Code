package com.schooltech.sms.controller.fileupload;

import com.schooltech.sms.service.file.ExcelCircularFileService;
import com.schooltech.sms.service.file.ExcelStudentFileService;
import com.schooltech.sms.service.file.ExcelStudentFullFileService;
import com.schooltech.sms.service.file.ExcelTeacherFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ExcelFileController {
    @Autowired
    ExcelStudentFileService excelStudentFileService;
    @Autowired
    ExcelStudentFullFileService excelStudentFullFileService;
    @Autowired
    ExcelTeacherFileService excelTeacherFileService;
    //    @Autowired
//    ExcelAdminFileService excelAdminFileService;
    @Autowired
    ExcelCircularFileService excelCircularFileService;

    @PostMapping("/uploadClassCircularExcel")
    public ResponseEntity<String> uploadClassCircularExcel(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (!fileName.equals("Class_Circular.xlsx")) {
            throw new RuntimeException("Uploaded Wrong file!");
        }
        return excelCircularFileService.processClassCircularExcelData(file);
    }

    @PostMapping("/uploadBusCircularExcel")
    public ResponseEntity<String> uploadBusCircularExcel(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (!fileName.equals("Bus_Circular.xlsx")) {
            throw new RuntimeException("Uploaded Wrong file!");
        }
        return excelCircularFileService.processBusCircularExcelData(file);
    }

    @PostMapping("/uploadStudentParentExcel")
    @Transactional
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader String tenantId) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.contains("Student_Parent_")) {
            throw new RuntimeException("Uploaded Wrong file!");
        }
        return excelStudentFullFileService.processStudentParentExcelData(file, tenantId);
    }

    @PostMapping("/uploadTeacherExcel")
    @Transactional
    public ResponseEntity<String> uploadTeacherExcel(@RequestParam("file") MultipartFile file, @RequestHeader String tenantId) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.equals("Teacher.xlsx")) {
            throw new RuntimeException("Uploaded Wrong file!");
        }
        return excelTeacherFileService.processTeacherExcelData(file, tenantId);
    }


//    @PostMapping("/uploadAdminExcel")
//    @Transactional
//    public ResponseEntity<String> uploadAdminExcel(@RequestParam("file") MultipartFile file, @RequestHeader String tenantId) throws IOException {
//        String fileName = file.getOriginalFilename();
//        if (!fileName.equals("Admin.xlsx")) {
//            throw new RuntimeException("Uploaded Wrong file!");
//        }
//        return excelAdminFileService.processAdminExcelData(file, tenantId);
//    }

    //    @PostMapping("/uploadHolidayCircularExcel")
//    public ResponseEntity<String> uploadHolidayCircularExcel(@RequestParam("file") MultipartFile file) throws IOException {
//        String fileName = file.getOriginalFilename();
//        if (!fileName.equals("Holiday_Circular.xlsx")) {
//            throw new RuntimeException("Uploaded Wrong file!");
//        }
//        return excelCircularFileService.processHolidayCircularExcelData(file);
//    }


}
