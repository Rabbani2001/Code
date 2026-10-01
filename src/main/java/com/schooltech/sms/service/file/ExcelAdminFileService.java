//package com.schooltech.sms.service.file;
//
//import com.schooltech.security.jwt.service.AuthService;
//import com.schooltech.sms.constant.AppConstant;
//import com.schooltech.sms.dao.client.admin.AdminRepository;
//import com.schooltech.sms.dao.client.circulars.BusCircularRepository;
//import com.schooltech.sms.dao.client.user.UserRepository;
//import com.schooltech.sms.entity.client.admin.Admin;
//import com.schooltech.sms.entity.client.circulars.BusCircular;
//import com.schooltech.sms.entity.client.user.User;
//import com.schooltech.sms.service.admin.AdminService;
//import com.schooltech.sms.controller.admin.dto.AdminUserDTO;
//import com.schooltech.sms.utility.ExcelFileUtility;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class ExcelAdminFileService {
//    private static final Logger log = LoggerFactory.getLogger(ExcelAdminFileService.class);
//    private static final String COL_ADD = "Add";
//
//    private static final String COL_IS_ACTIVE = "is Active";
//    private static final String COL_FULL_NAME = "Full Name";
//    private static final String COL_DESIGNATION = "Designation";
//    private static final String COL_CLASS_NAMES = "Class Name";
//    private static final String COL_EMAIL = "Email";
//    private static final String COL_PHONE = "Phone No";
//    private static final String COL_EMPLOYEE_ID = "Employee Id";
//    private static final String COL_AADHAR = "Aadhar No";
//    @Autowired
//    private AdminService adminService;
//    @Autowired
//    private AdminRepository adminRepository;
//    @Autowired
//    private BusCircularRepository busCircularRepository;
//    @Autowired
//    private AuthService authService;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public ResponseEntity<String> processAdminExcelData(MultipartFile file, String tenantId) throws IOException {
//        try (InputStream inputStream = file.getInputStream();
//             Workbook workbook = WorkbookFactory.create(inputStream)) {
//            processSheet1(workbook.getSheetAt(0), file.getOriginalFilename(), tenantId); // First sheet
//        }
//        return ResponseEntity.ok().body("Uploaded  Admins successfully");
//    }
//
//    public void processSheet1(Sheet sheet, String fileName, String tenantId) {
//        //List<AdminUserDTO> adminUserDTOList = new ArrayList<>();
//        AdminUserDTO adminUserDTO = new AdminUserDTO();
////        List<User> userListForAdmin = new ArrayList<>();
////        List<Admin> adminList = new ArrayList<>();
//        //Existing DB data
//        List<User> userListDB = userRepository.findAll();
//        List<Admin> adminListDB = adminRepository.findAll();
//        List<BusCircular> busCircularsDB = busCircularRepository.findAll();
//
//        Set<String> excelEmployeeIds = new HashSet<>();
//        Set<String> excelEmails = new HashSet<>();
//        Set<String> excelPhoneNos = new HashSet<>();
//        Set<String> excelAadharNos = new HashSet<>();
//
//        Iterator<Row> rowIterator = sheet.iterator();
//        int rowIndex = 0;
//        while (rowIterator.hasNext()) {
//            Row row = rowIterator.next();
//            if (rowIndex < 1) { // Skip header row
//                rowIndex++;
//                continue;
//            }
//            if (ExcelFileUtility.isRowEmpty(row)) { // Skip empty rows
//                continue;
//            }
//            String isAddAllowed = ExcelFileUtility.getCellValue(row, 0, rowIndex, fileName);
//            if (isAddAllowed == null)
//                throwExcelException(fileName, rowIndex, COL_ADD, "Add column cannot be empty or", isAddAllowed);
//            if (!isAddAllowed.equalsIgnoreCase("yes")) {
//                continue;
//            }
//
//            int columnIndex = 0;
//            Boolean isActive = ExcelFileUtility.parseBooleanNew(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName), fileName, rowIndex, COL_IS_ACTIVE);
//            String fullName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String adminEmployeeId = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String phoneNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String classNames = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String designation = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String email = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String altPhoneNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String gender = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String aadharNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String fatherName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String motherName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String guardianName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String qualification = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            LocalDate dob = ExcelFileUtility.parseDateDDhyphenMMhyphenYY(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
//            String permAddress = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            String currentAddress = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            LocalDate joiningDate = ExcelFileUtility.parseDateDDhyphenMMhyphenYY(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
//            String busRoute = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
//            log.info("SchoolTech.... Processed " + columnIndex + " fields in file: " + fileName);
//
//            //excel mand fields to validate :-  phoneNo,adminEmployeeId
//            //excel non-mand fields to validate :- email ,aadharNo
//            //excel mand fields :-  fullName, classNames, designation, isActive(already validated while fetching from excel)
//
//            //mandatory field check
//            if (fullName == null || designation == null) {
//                String columnNames = COL_FULL_NAME + " or " + COL_DESIGNATION;
//                throwExcelException(fileName, rowIndex, columnNames, "Row cannot be empty or null", "\" or null");
//            }
//
//            if (adminEmployeeId != null) {
//                if (!excelEmployeeIds.add(adminEmployeeId))
//                    throwExcelException(fileName, rowIndex, COL_EMPLOYEE_ID, "Duplicate EMPLOYEE ID found in Excel", adminEmployeeId);
//                validateAdminEmployeeIdInExcel(userListDB, adminEmployeeId, fileName);
//            } else {
//                throwExcelException(fileName, rowIndex, COL_EMPLOYEE_ID, "EMPLOYEE ID is mandatory", "EMPTY");
//            }
//
//            if (phoneNo != null) {
//                if (!excelPhoneNos.add(phoneNo))
//                    throwExcelException(fileName, rowIndex, COL_PHONE, "Duplicate Phone Number found in Excel", phoneNo);
//                validateAdminPhoneNoInExcel(userListDB, phoneNo, fileName);
//            } else {
//                throwExcelException(fileName, rowIndex, COL_PHONE, "Phone Number is mandatory", "EMPTY");
//            }
//
//            if (email != null) {
//                email = email.toLowerCase();
//                if (!excelEmails.add(email))
//                    throwExcelException(fileName, rowIndex, COL_EMAIL, "Duplicate Email found in Excel", email);
//                validateAdminEmailInExcel(userListDB, email, fileName);
//            }
//
//            if (aadharNo != null) {
//                if (!excelAadharNos.add(aadharNo))
//                    throwExcelException(fileName, rowIndex, COL_AADHAR, "Duplicate Aadhar Number found in Excel", aadharNo);
//                validateAdminAadharNoInExcel(adminListDB, aadharNo, fileName);
//            }
//
//            if (busRoute != null) {
//                validateAdminBusRouteInExcel(busCircularsDB, busRoute, fileName);
//            }
//
//            User user = new User();
//            //user.setUsername(adminEmployeeId);
//            user.setEmail(email);
//            user.setPhoneNo(phoneNo);
//            user.setIsActive(isActive);
//            user.setRole(AppConstant.ADMIN_ROLE);
//            user.setPassword(AppConstant.DEFAULT_PASSWORD); //Fixed values
//            user.setTenantId(tenantId);//Fixed values
//            adminUserDTO.setUser(user);
//            //userListForAdmin.add(user);
//
//
//            Admin admin = new Admin();
//            //admin.setUsername(adminEmployeeId);
//            admin.setFullName(fullName);
//            admin.setGender(gender);
//            admin.setAadharNo(aadharNo);
//            List<String> classNamesList = classNames != null ? List.of(classNames.split(",")) : new ArrayList<>();
//            admin.setClassNames(classNamesList);
//            admin.setFatherName(fatherName);
//            admin.setMotherName(motherName);
//            admin.setGuardianName(guardianName);
//            admin.setQualification(qualification);
//            admin.setDesignation(designation);
//            admin.setDob(dob);
//            admin.setPermanentAddress(permAddress);
//            admin.setCurrentAddress(currentAddress);
//            admin.setJoiningDate(joiningDate);
//            admin.setBusRoute(busRoute);
//            //String photoUrl = "https://schooltech-s3.s3.ap-south-1.amazonaws.com/" + ExcelFileUtility.getStringCellValue(row, 20) + "/images/admins/" + ExcelFileUtility.getStringCellValue(row, 1).toLowerCase() + ".jpg";
//            //admin.setPhotoUrl(photoUrl);
//            //adminList.add(admin);
//            adminUserDTO.setAdmin(admin);
//            //----------------------------
//            rowIndex++;
//        }
//        adminService.saveAdminsWithEntity(adminUserDTO);
//    }
//
//    private void validateAdminEmployeeIdInExcel(List<User> userListDB, String adminEmployeeId, String excelFileName) {
//        Set<String> employeeIdsInUserListDB =
//                userListDB.stream()
//                        .filter(u -> u != null && u.getUsername() != null && !u.getUsername().isBlank())
//                        .map(u -> u.getUsername().trim().toLowerCase())
//                        .collect(Collectors.toSet());
//        if (employeeIdsInUserListDB.contains(adminEmployeeId)) {
//            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + " has Employee Id: \"" + adminEmployeeId + "\" already exists in User DB ");
//        }
//    }
//
//    private void validateAdminPhoneNoInExcel(List<User> userListDB, String phoneNo, String excelFileName) {
//        Set<String> phoneNosInUserListDB =
//                userListDB.stream()
//                        .filter(u -> u != null && u.getPhoneNo() != null && !u.getPhoneNo().isBlank())
//                        .map(u -> u.getPhoneNo().trim().toLowerCase())
//                        .collect(Collectors.toSet());
//        if (phoneNosInUserListDB.contains(phoneNo)) {
//            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + " has PhoneNo: \"" + phoneNo + "\" already exists in User DB");
//        }
//    }
//
//    private void validateAdminEmailInExcel(List<User> userListDB, String email, String excelFileName) {
//        Set<String> emailsInUserListDB =
//                userListDB.stream()
//                        .filter(u -> u != null && u.getEmail() != null && !u.getEmail().isBlank())
//                        .map(u -> u.getEmail().trim().toLowerCase())
//                        .collect(Collectors.toSet());
//        if (emailsInUserListDB.contains(email)) {
//            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + " has Email: \"" + email + "\" already exists in User DB ");
//        }
//
//    }
//
//    private void validateAdminAadharNoInExcel(List<Admin> adminListDB, String aadharNo, String excelFileName) {
//        Set<String> aadharNoInAdminListDB =
//                adminListDB.stream()
//                        .filter(a -> a != null && a.getAadharNo() != null && !a.getAadharNo().isBlank())
//                        .map(a -> a.getAadharNo().trim().toLowerCase())
//                        .collect(Collectors.toSet());
//        if (aadharNoInAdminListDB.contains(aadharNo)) {
//            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + " has AadharNo: \"" + aadharNo + "\" already exists in Admin DB ");
//        }
//    }
//
//    private void validateAdminBusRouteInExcel(List<BusCircular> busCircularsDB, String busRoute, String excelFileName) {
//        Set<String> busRoutesInDB =
//                busCircularsDB.stream()
//                        .filter(t -> t != null && t.getBusRoute() != null && !t.getBusRoute().isBlank())
//                        .map(t -> t.getBusRoute().trim().toLowerCase())
//                        .collect(Collectors.toSet());
//        if (!busRoutesInDB.contains(busRoute.toLowerCase())) {
//            throw new RuntimeException("OPERATION ABORTED: " + excelFileName + "has Bus Route: \"" + busRoute + "\" doesn't exists in DB: ");
//        }
//    }
//
//    private void throwExcelException(
//            String fileName,
//            int rowIndex,
//            String columnName,
//            String message,
//            String value
//    ) {
//        throw new RuntimeException(
//                "OPERATION ABORTED: " + fileName +
//                        " | Row " + (rowIndex + 1) +
//                        " | Column \"" + columnName + "\"" +
//                        " | Value: \"" + value + "\"" +
//                        " | Reason: " + message
//        );
//    }
//
//
//}
