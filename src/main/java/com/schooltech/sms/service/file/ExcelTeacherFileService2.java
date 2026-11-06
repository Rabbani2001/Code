package com.schooltech.sms.service.file;

import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.teacher.dto.TeacherUserDTO;
import com.schooltech.sms.dao.client.circulars.BusCircularRepository;
import com.schooltech.sms.dao.client.teacher.TeacherRepository;
import com.schooltech.sms.dao.client.user.UserRepository;
import com.schooltech.sms.entity.client.circulars.BusCircular;
import com.schooltech.sms.entity.client.teacher.Teacher;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.service.teacher.TeacherService;
import com.schooltech.sms.utility.ExcelFileUtility;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelTeacherFileService2 {
    private static final Logger log = LoggerFactory.getLogger(ExcelTeacherFileService2.class);
    private static final String COL_ADD = "Add";
    private static final String COL_FULL_NAME = "Full Name";
    private static final String COL_DESIGNATION = "Designation";
    private static final String COL_CLASS_NAMES = "Class Name";
    private static final String COL_EMAIL = "Email";
    private static final String COL_PHONE = "Phone No";
    private static final String COL_EMPLOYEE_ID = "Employee Id";
    private static final String COL_AADHAR = "Aadhar No";
    private static final String COL_BUS_ROUTE = "Bus Route";
    private static final String COL_IS_ACTIVE = "is Active";
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private BusCircularRepository busCircularRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<String> processTeacherExcelData(MultipartFile file, String tenantId) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            processSheet1(workbook.getSheetAt(0), file.getOriginalFilename(), tenantId); // First sheet
        }
        return ResponseEntity.ok().body("Uploaded Teachers successfully");
    }

    public void processSheet1(Sheet sheet, String fileName, String tenantId) {
        List<TeacherUserDTO> teacherUserDTOList = new ArrayList<>();
//        List<User> userListForTeacher = new ArrayList<>();
//        List<Teacher> teacherList = new ArrayList<>();
        //Existing DB data
        List<User> userListDB = userRepository.findAll();
        List<Teacher> teacherListDB = teacherRepository.findAll();
        List<BusCircular> busCircularsDB = busCircularRepository.findAll();

        Set<String> excelEmployeeIds = new HashSet<>();
        Set<String> excelEmails = new HashSet<>();
        Set<String> excelPhoneNos = new HashSet<>();
        Set<String> excelAadharNos = new HashSet<>();

        Iterator<Row> rowIterator = sheet.iterator();
        int rowIndex = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (rowIndex < 1) { // Skip header row
                rowIndex++;
                continue;
            }
            if (ExcelFileUtility.isRowEmpty(row)) { // Skip empty rows
                continue;
            }
            String isAddAllowed = ExcelFileUtility.getCellValue(row, 0, rowIndex, fileName);
            if (isAddAllowed == null)
                throwExcelException(fileName, rowIndex, COL_ADD, "Add column cannot be empty or", isAddAllowed);
            if (!isAddAllowed.equalsIgnoreCase("yes")) {
                continue;
            }

            TeacherUserDTO teacherUserDTO = new TeacherUserDTO();
            int columnIndex = 0;
            Boolean isActive = ExcelFileUtility.parseBooleanNew(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName), fileName, rowIndex, COL_IS_ACTIVE);
            String fullName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String teacherEmployeeId = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String phoneNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String classNames = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String designation = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String email = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String altPhoneNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String gender = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String aadharNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String fatherName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String motherName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String guardianName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String qualification = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate dob = ExcelFileUtility.parseDateDDhyphenMMhyphenYY(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            String permAddress = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String currentAddress = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate joiningDate = ExcelFileUtility.parseDateDDhyphenMMhyphenYY(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            String busRoute = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            log.info("SchoolTech.... Processed " + columnIndex + " fields in file: " + fileName);

            //excel mand fields to validate :-  phoneNo,teacherEmployeeId
            //excel non-mand fields to validate :- email ,aadharNo
            //excel mand fields :-  fullName, classNames, designation, isActive(already validated while fetching from excel)

            //mandatory field check
            if (fullName == null || classNames == null || designation == null) {
                String columnNames = COL_FULL_NAME + " or " + COL_CLASS_NAMES + " or " + COL_DESIGNATION;
                throwExcelException(fileName, rowIndex, columnNames, "Row cannot be empty or null", "\" or null");
            }

            if (teacherEmployeeId != null) {
                if (!excelEmployeeIds.add(teacherEmployeeId))
                    throwExcelException(fileName, rowIndex, COL_EMPLOYEE_ID, "Duplicate EMPLOYEE ID found in Excel", teacherEmployeeId);
                validateTeacherEmployeeIdInExcel(userListDB, teacherEmployeeId, fileName);
            } else {
                throwExcelException(fileName, rowIndex, COL_EMPLOYEE_ID, "EMPLOYEE ID is mandatory", "EMPTY");
            }


            if (phoneNo != null) {
                if (!excelPhoneNos.add(phoneNo))
                    throwExcelException(fileName, rowIndex, COL_PHONE, "Duplicate Phone Number found in Excel", phoneNo);
                validateTeacherPhoneNoInExcel(userListDB, phoneNo, fileName);
            } else {
                throwExcelException(fileName, rowIndex, COL_PHONE, "Phone Number is mandatory", "EMPTY");
            }

            if (email != null) {
                email = email.toLowerCase();
                if (!excelEmails.add(email))
                    throwExcelException(fileName, rowIndex, COL_EMAIL, "Duplicate Email found in Excel", email);
                validateTeacherEmailInExcel(userListDB, email, fileName);
            }

            if (aadharNo != null) {
                if (!excelAadharNos.add(aadharNo))
                    throwExcelException(fileName, rowIndex, COL_AADHAR, "Duplicate Aadhar Number found in Excel", aadharNo);
                validateTeacherAadharNoInExcel(teacherListDB, aadharNo, fileName);
            }

            if (busRoute != null) {
                validateTeacherBusRouteInExcel(busCircularsDB, busRoute, fileName);
            }

            User user = new User();
            //user.setUsername(teacherEmployeeId);
            user.setEmail(email);
            user.setPhoneNo(phoneNo);
            user.setIsActive(isActive);
            user.setRole(AppConstant.TEACHER_ROLE);
            user.setPassword(AppConstant.DEFAULT_PASSWORD); //Fixed values
            user.setTenantId(tenantId);//Fixed values
            //userListForTeacher.add(user);
            teacherUserDTO.setUser(user);

            Teacher teacher = new Teacher();
            //teacher.setUsername(teacherEmployeeId);
            teacher.setFullName(fullName);//6,7 are is active & role
            teacher.setGender(gender);
            teacher.setAadharNo(aadharNo);
            List<String> classNamesList = classNames != null ? List.of(classNames.split(",")) : new ArrayList<>();
            teacher.setClassNames(classNamesList);
            teacher.setFatherName(fatherName);
            teacher.setMotherName(motherName);
            teacher.setGuardianName(guardianName);
            teacher.setQualification(qualification);
            teacher.setDesignation(designation);
            teacher.setDob(dob);
            teacher.setPermanentAddress(permAddress);
            teacher.setCurrentAddress(currentAddress);
            teacher.setJoiningDate(joiningDate);
            teacher.setBusRoute(busRoute);
            //String photoUrl = "https://schooltech-s3.s3.ap-south-1.amazonaws.com/" + ExcelFileUtility.getStringCellValue(row, 20) + "/images/teachers/" + ExcelFileUtility.getStringCellValue(row, 1).toLowerCase() + ".jpg";
            //teacher.setPhotoUrl(photoUrl);
            //teacherList.add(teacher);
            teacherUserDTO.setTeacher(teacher);

            teacherUserDTOList.add(teacherUserDTO);
            //----------------------------
            rowIndex++;
        }
//        CreateTeacherDTO createTeacherDTO = new CreateTeacherDTO();
//        createTeacherDTO.setTeacherList(teacherList);
//        createTeacherDTO.setUserList(userListForTeacher);
        //teacherService.saveTeachersWithEntity(createTeacherDTO);
        teacherService.saveTeachersWithEntity(teacherUserDTOList);
    }

    private void validateTeacherEmployeeIdInExcel(List<User> userListDB, String teacherEmployeeId, String excelFileName) {
        Set<String> employeeIdsInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getUsername() != null && !u.getUsername().isBlank())
                        .map(u -> u.getUsername().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (employeeIdsInUserListDB.contains(teacherEmployeeId)) {
            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + " has Employee Id: \"" + teacherEmployeeId + "\" already exists in User DB ");
        }
    }

    private void validateTeacherPhoneNoInExcel(List<User> userListDB, String phoneNo, String excelFileName) {
        Set<String> phoneNosInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getPhoneNo() != null && !u.getPhoneNo().isBlank())
                        .map(u -> u.getPhoneNo().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (phoneNosInUserListDB.contains(phoneNo)) {
            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + " has PhoneNo: \"" + phoneNo + "\" already exists in User DB ");
        }
    }

    private void validateTeacherEmailInExcel(List<User> userListDB, String email, String excelFileName) {
        Set<String> emailsInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getEmail() != null && !u.getEmail().isBlank())
                        .map(u -> u.getEmail().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (emailsInUserListDB.contains(email)) {
            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + " has Email: \"" + email + "\" already exists in User DB ");
        }
    }

    private void validateTeacherAadharNoInExcel(List<Teacher> teacherListDB, String aadharNo, String excelFileName) {
        Set<String> aadharNoInTeacherListDB =
                teacherListDB.stream()
                        .filter(t -> t != null && t.getAadharNo() != null && !t.getAadharNo().isBlank())
                        .map(t -> t.getAadharNo().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (aadharNoInTeacherListDB.contains(aadharNo)) {
            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + " has AadharNo: \"" + aadharNo + "\" already exists in Teacher DB: ");
        }
    }

    private void validateTeacherBusRouteInExcel(List<BusCircular> busCircularsDB, String busRoute, String excelFileName) {
        Set<String> busRoutesInDB =
                busCircularsDB.stream()
                        .filter(t -> t != null && t.getBusRoute() != null && !t.getBusRoute().isBlank())
                        .map(t -> t.getBusRoute().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (!busRoutesInDB.contains(busRoute.toLowerCase())) {
            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + "has Bus Route: \"" + busRoute + "\" doesn't exists in DB: ");
        }
    }

    private void throwExcelException(
            String fileName,
            int rowIndex,
            String columnName,
            String message,
            String value
    ) {
        throw new RuntimeException(
                "OPERATION ABORTED: " + fileName +
                        " | Row " + (rowIndex + 1) +
                        " | Column \"" + columnName + "\"" +
                        " | Value: \"" + value + "\"" +
                        " | Reason: " + message
        );
    }


}
