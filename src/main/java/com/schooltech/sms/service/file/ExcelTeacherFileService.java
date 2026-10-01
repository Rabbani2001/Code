package com.schooltech.sms.service.file;

import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.teacher.dto.TeacherUserDTO;
import com.schooltech.sms.dao.client.circulars.BusCircularRepository;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.dao.client.teacher.TeacherRepository;
import com.schooltech.sms.dao.client.user.UserRepository;
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.teacher.Teacher;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.service.teacher.TeacherService;
import com.schooltech.sms.utility.AppUtility;
import com.schooltech.sms.utility.DateUtility;
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

import static com.schooltech.sms.constant.AppConstant.DESIGNATION_DROPDOWN_OPTIONS;

@Service
public class ExcelTeacherFileService {
    private static final Logger log = LoggerFactory.getLogger(ExcelTeacherFileService.class);
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
    @Autowired
    private ClassCircularRepository classCircularRepository;


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
        List<ClassCircular> classCircularsDB = classCircularRepository.findBySession(DateUtility.getCurrentAcademicSession());

        Set<String> excelEmployeeIds = new HashSet<>();
        Set<String> excelEmails = new HashSet<>();
        Set<String> excelPhoneNos = new HashSet<>();


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
                throwExcelException(fileName, rowIndex, COL_ADD, isAddAllowed, "Add column cannot be empty or null");
            if (!isAddAllowed.equalsIgnoreCase("yes")) {
                rowIndex++;
                continue;
            }
            TeacherUserDTO teacherUserDTO = new TeacherUserDTO();
            int columnIndex = 0;
            Boolean isActive = ExcelFileUtility.parseBooleanNew(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName), fileName, rowIndex, COL_IS_ACTIVE);
            String teacherEmployeeId = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String fullName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String classNames = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String phoneNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String email = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String designation = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String fatherName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String motherName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String gender = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);

            String dobRaw = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate dob = dobRaw != null
                    ? ExcelFileUtility.parseDateDDhyphenMMhyphenYY(dobRaw)
                    : null;

            String joiningDateRaw = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate joiningDate = joiningDateRaw != null
                    ? ExcelFileUtility.parseDateDDhyphenMMhyphenYY(joiningDateRaw)
                    : null;

            String altPhoneNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String currentAddress = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String permanentAddress = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String qualification = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);

            log.info("SchoolTech.... Processed " + columnIndex + " fields in file: " + fileName);

            //mandatory field check
            if (fullName == null) {
                throwExcelException(fileName, rowIndex, COL_FULL_NAME, fullName, "FullName cannot be empty or null");
            }
            //mandatory field check
            if (classNames != null) {
                List<String> classNamesList = List.of(classNames.split(","));
                validateClassNamePresentInClassCircularInExcel(classCircularsDB, classNamesList, fileName, rowIndex);
            } else {
                throwExcelException(fileName, rowIndex, COL_CLASS_NAMES, classNames, "ClassNames cannot be empty or null");
            }
            //mandatory field check
            if (designation != null) {
                if (!DESIGNATION_DROPDOWN_OPTIONS.contains(designation)) {
                    throwExcelException(fileName, rowIndex, COL_DESIGNATION, designation, "Designation allowed values are: " + DESIGNATION_DROPDOWN_OPTIONS);
                }
            } else {
                throwExcelException(fileName, rowIndex, COL_DESIGNATION, designation, "Designation cannot be empty or null");
            }

            if (teacherEmployeeId != null) {
                if (!excelEmployeeIds.add(teacherEmployeeId))
                    throwExcelException(fileName, rowIndex, COL_EMPLOYEE_ID, teacherEmployeeId, "Duplicate teacher EMPLOYEE ID found in Excel");
                validateTeacherEmployeeIdInExcel(userListDB, teacherEmployeeId, fileName, rowIndex);
            }

            if (phoneNo != null) {
                if (!AppUtility.isValidPhoneNumber(phoneNo)) {
                    throwExcelException(fileName, rowIndex, COL_PHONE, phoneNo, "Phone Number must be of digits & only numbers");
                }
                if (!excelPhoneNos.add(phoneNo))
                    throwExcelException(fileName, rowIndex, COL_PHONE, phoneNo, "Duplicate Phone Number found in Excel");
                validateTeacherPhoneNoInExcel(userListDB, phoneNo, fileName, rowIndex);
            } else {
                throwExcelException(fileName, rowIndex, COL_PHONE, phoneNo, "Phone Number cannot be empty or null");
            }

            if (email != null) {
                if (!AppUtility.isValidEmail(email)) {
                    throwExcelException(fileName, rowIndex, COL_EMAIL, email, "Email is Invalid");
                }
                if (!excelEmails.add(email))
                    throwExcelException(fileName, rowIndex, COL_EMAIL, email, "Duplicate Email found in Excel");
                validateTeacherEmailInExcel(userListDB, email, fileName, rowIndex);
            }

            User user = new User();
            //user.setUsername(teacherEmployeeId);
            user.setEmail(email);
            user.setPhoneNo(phoneNo);
            user.setAltPhoneNo(altPhoneNo);
            user.setIsActive(isActive);
            user.setRole(AppConstant.TEACHER_ROLE);
            user.setPassword(AppConstant.DEFAULT_PASSWORD); //Fixed values
            user.setTenantId(tenantId);//Fixed values
            //userListForTeacher.add(user);
            teacherUserDTO.setUser(user);

            Teacher teacher = new Teacher();
            //teacher.setUsername(teacherEmployeeId);
            teacher.setEmployeeId(teacherEmployeeId);
            teacher.setFullName(fullName);//6,7 are is active & role


            List<String> classNamesList = classNames != null
                    ? Arrays.stream(classNames.split(","))
                    .map(String::trim)          // optional: remove spaces
                    .collect(Collectors.toList())
                    : new ArrayList<>();

            teacher.setClassNames(classNamesList);
            teacher.setDesignation(designation);
            teacher.setFatherName(fatherName);
            teacher.setMotherName(motherName);
            teacher.setGender(gender);
            teacher.setDob(dob);
            teacher.setJoiningDate(joiningDate);
            teacher.setCurrentAddress(currentAddress);
            teacher.setPermanentAddress(permanentAddress);
            teacher.setQualification(qualification);
            teacherUserDTO.setTeacher(teacher);
            Map<String, String> teacherDocuments = new HashMap<>();
            teacher.setDocuments(teacherDocuments);
            //teacherList.add(teacher);
            teacherUserDTOList.add(teacherUserDTO);
            //----------------------------
            rowIndex++;
        }
        teacherService.saveTeachersWithEntity(teacherUserDTOList);
    }

    private void validateClassNamePresentInClassCircularInExcel(List<ClassCircular> classCircularsDB, List<String> classNames, String excelFileName, int rowIndex) {
        Set<String> classCirculars =
                classCircularsDB.stream()
                        .filter(t -> t != null && t.getClassName() != null && !t.getClassName().isBlank())
                        .map(t -> t.getClassName().trim())
                        .collect(Collectors.toSet());

        classNames.forEach(className -> {
            if (!classCirculars.contains(className)) {
                throwExcelException(excelFileName, rowIndex, COL_CLASS_NAMES, className, "Not present in class circular data");
            }
        });
    }

    private void validateTeacherEmployeeIdInExcel(List<User> userListDB, String teacherEmployeeId, String excelFileName, int rowIndex) {
        Set<String> employeeIdsInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getUsername() != null && !u.getUsername().isBlank())
                        .map(u -> u.getUsername().trim())
                        .collect(Collectors.toSet());
        if (employeeIdsInUserListDB.contains(teacherEmployeeId)) {
            throwExcelException(excelFileName, rowIndex, COL_EMPLOYEE_ID, teacherEmployeeId, "Employee Id already exists in Teacher Data ");
        }
    }

    private void validateTeacherPhoneNoInExcel(List<User> userListDB, String phoneNo, String excelFileName, int rowIndex) {
        Set<String> phoneNosInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getPhoneNo() != null && !u.getPhoneNo().isBlank())
                        .map(u -> u.getPhoneNo().trim())
                        .collect(Collectors.toSet());
        if (phoneNosInUserListDB.contains(phoneNo)) {
            throwExcelException(excelFileName, rowIndex, COL_PHONE, phoneNo, "Phone Number already exists in Teacher User data");
        }
    }

    private void validateTeacherEmailInExcel(List<User> userListDB, String email, String excelFileName, int rowIndex) {
        Set<String> emailsInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getEmail() != null && !u.getEmail().isBlank())
                        .map(u -> u.getEmail().trim())
                        .collect(Collectors.toSet());
        if (emailsInUserListDB.contains(email)) {
            throwExcelException(excelFileName, rowIndex, COL_EMAIL, email, "Email already exists in Teacher User data");
        }
    }

    private void throwExcelException(
            String fileName,
            int rowIndex,
            String columnName,
            String value,
            String message

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
