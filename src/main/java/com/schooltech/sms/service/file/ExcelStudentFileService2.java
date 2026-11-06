package com.schooltech.sms.service.file;

import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.student.dto.StudentParentUserDTO;
import com.schooltech.sms.dao.client.circulars.BusCircularRepository;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.dao.client.student.ParentRepository;
import com.schooltech.sms.dao.client.student.StudentOtherInfoRepository;
import com.schooltech.sms.dao.client.student.StudentRepository;
import com.schooltech.sms.dao.client.user.UserRepository;
import com.schooltech.sms.entity.client.circulars.BusCircular;
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.student.Parent;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.student.StudentOtherInfo;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.service.student.ParentService;
import com.schooltech.sms.service.student.StudentFeeService;
import com.schooltech.sms.service.student.StudentService;
import com.schooltech.sms.utility.DateUtility;
import com.schooltech.sms.utility.ExcelFileUtility;
import com.schooltech.sms.utility.UsernameUtility;
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

import static com.schooltech.sms.constant.AppConstant.PARENT_ACTIONS;

@Service
public class ExcelStudentFileService2 {
    private static final Logger log = LoggerFactory.getLogger(ExcelStudentFileService2.class);
    private static final String COL_ADD = "Add";
    private static final String COL_IS_ACTIVE = "is Active";
    private static final String COL_IS_SIBLING = "Is Sibling";
    private static final String COL_STUDENT_FULL_NAME = "Student Full Name";
    private static final String COL_ADMISSION_NO = "Admission No";
    private static final String COL_PARENT_TYPE = "Parent Type";
    private static final String COL_PHONE = "Phone No";
    private static final String COL_EMAIL = "Email";
    private static final String COL_PARENT_EMAIL = "Email";
    private static final String COL_FATHER_NAME = "Father Name";
    private static final String COL_MOTHER_NAME = "Mother Name";
    private static final String COL_GUARDIAN_NAME = "Guardian Name";
    private static final String COL_AADHAR_FATHER = "Father Aadhar No";
    private static final String COL_AADHAR_MOTHER = "Mother Aadhar No";
    private static final String COL_AADHAR_GUARDIAN = "Guardian Aadhar No";
    private static final String COL_AADHAR_STUDENT = "Student Aadhar No";
    private static final String COL_APAAR = "Student Apaar";
    private static final String COL_BUS_ROUTE = "Bus Route";

    @Autowired
    private StudentService studentService;
    @Autowired
    private ParentService parentService;
    @Autowired
    private StudentFeeService studentFeeService;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentOtherInfoRepository studentOtherInfoRepository;
    @Autowired
    private BusCircularRepository busCircularRepository;
    @Autowired
    private ClassCircularRepository classCircularRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UsernameUtility usernameUtility;

    public static String incrementEndingNumber(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        // Match prefix and trailing digits
        String regex = "(.*?)(\\d+)$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Input must end with a number");
        }

        String prefix = matcher.group(1);
        int number = Integer.parseInt(matcher.group(2));

        return prefix + (number + 1);
    }

    public ResponseEntity<String> processStudentParentExcelData(MultipartFile file, String tenantId) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            processSheet1(workbook.getSheetAt(0), file.getOriginalFilename(), tenantId);
        }
        return ResponseEntity.ok().body("Uploaded Students & Parents successfully.");
    }

    public void processSheet1(Sheet sheet, String fileName, String tenantId) {
//        List<User> userList = new ArrayList<>();
//        List<Parent> parentList = new ArrayList<>();
//        List<Student> studentList = new ArrayList<>();
//        List<StudentOtherInfo> studentOtherInfoList = new ArrayList<>();
        List<StudentParentUserDTO> studentParentUserDTOList = new ArrayList<>();
        //Existing DB data
        List<User> userListDB = userRepository.findAll();
        List<Parent> parentListDB = parentRepository.findAll();
        List<Student> studentListDB = studentRepository.findAll();
        List<StudentOtherInfo> studentOtherInfoListDB = studentOtherInfoRepository.findAll();
        List<BusCircular> busCircularsDB = busCircularRepository.findAll();//busCircularRepository.findAllBusRoutesBySession(DateUtility.getCurrentAcademicSession());
        List<ClassCircular> classCircularsDB = classCircularRepository.findBySession(DateUtility.getCurrentAcademicSession());//busCircularRepository.findAllBusRoutesBySession(DateUtility.getCurrentAcademicSession());


        //Parent
        //Set<String> excelParentUserIds = new HashSet<>();
        Set<String> excelParentEmails = new HashSet<>();
        Set<String> excelParentPhoneNos = new HashSet<>();
        Set<String> excelParentAadharNos = new HashSet<>();
        //Father,mother,guardian aaadhar
        Set<String> excelFatherAadharNos = new HashSet<>();
        Set<String> excelMotherAadharNos = new HashSet<>();
        Set<String> excelGuardianAadharNos = new HashSet<>();
        //Student
        Set<String> excelStudentAdmissionNos = new HashSet<>();
        Set<String> excelStudentAadharNos = new HashSet<>();
        Set<String> excelStudentApaarNos = new HashSet<>();

        Iterator<Row> rowIterator = sheet.iterator();
        int rowIndex = 0;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (rowIndex < 1) { // Skip headers
                rowIndex++;
                continue;
            }
            if (ExcelFileUtility.isRowEmpty(row)) { // **Skip empty rows**
                continue;
            }
            String isAddAllowed = ExcelFileUtility.getCellValue(row, 0, rowIndex, fileName);
            if (isAddAllowed == null)
                throwExcelException(fileName, rowIndex, COL_ADD, isAddAllowed, "Add column cannot be empty or null");
            if (!isAddAllowed.equalsIgnoreCase("yes")) {
                continue;
            }

            StudentParentUserDTO studentParentUserDTO = new StudentParentUserDTO();

            String className = fileName.split("_")[2].replace(".xlsx", ""); //class Name is coming from excel file name

            int columnIndex = 0;
            Boolean isActive = ExcelFileUtility.parseBooleanNew(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName), fileName, rowIndex, COL_IS_ACTIVE);
            String isSibling = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String studentFullName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String admissionNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String parentPhoneNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String altPhoneNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String parentEmail = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String parentType = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String fatherName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String motherName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String guardianName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            //String className = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String studentGender = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate studentDob = ExcelFileUtility.parseDateDDhyphenMMhyphenYY(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            LocalDate admissionDate = ExcelFileUtility.parseDateDDhyphenMMhyphenYY(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            Long rollNo = ExcelFileUtility.parseLong(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            String studentAadharNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String apaarNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String fatherDOBExcel = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate fatherDob = fatherDOBExcel != null ? ExcelFileUtility.parseDateDDhyphenMMhyphenYY(fatherDOBExcel) : null;
            String fatherAadharNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String fatherEducation = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String fatherOccupation = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String motherDOBExcel = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate motherDob = motherDOBExcel != null ? ExcelFileUtility.parseDateDDhyphenMMhyphenYY(motherDOBExcel) : null;
            String motherAadharNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String motherEducation = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String motherOccupation = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String guardianDOBExcel = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate guardianDob = guardianDOBExcel != null ? ExcelFileUtility.parseDateDDhyphenMMhyphenYY(guardianDOBExcel) : null;
            String guardianAadharNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String guardianEducation = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String guardianOccupation = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String guardianGender = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String medium = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String permanentAddress = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String currentAddress = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String ruralOrUrban = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String busRoute = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String familyId = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String religion = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String caste = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String nationality = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String domicile = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String isMinority = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String minorityCategory = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String isBPL = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String stream = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String subjectsOpted = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String subjectsOptional = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String previousSchoolName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String previousAdmissionNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String previousSchoolCode = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String previousLeavingDate = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String previousClassPassed = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String previousMarksObtained = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            log.info("SchoolTech.... Processed " + columnIndex + " fields in file: " + fileName);


            //checking className is available in class circular or not
            //commenting this as className is checked at front end
            //validateClassNamePresentInClassCircularInExcel(classCircularsDB, className, fileName);

            //mandatory field check
            if (studentFullName == null) {
                throwExcelException(fileName, rowIndex, COL_STUDENT_FULL_NAME, studentFullName, " is empty or null");
            }
            //mandatory field check
            if (isSibling == null) {
                throwExcelException(fileName, rowIndex, COL_IS_SIBLING, isSibling, " is empty or null");
            }
            //mandatory field check
            if (admissionNo != null) {
                if (!excelStudentAdmissionNos.add(admissionNo))
                    throwExcelException(fileName, rowIndex, COL_ADMISSION_NO, admissionNo, "Duplicate Admission No found in Excel");
                validateStudentAdmissionNoInExcel(studentListDB, admissionNo, fileName, rowIndex);
            } else {
                throwExcelException(fileName, rowIndex, COL_ADMISSION_NO, admissionNo, " is empty or null");
            }


            //non mandatory field check
            if (studentAadharNo != null) {
                if (!excelStudentAadharNos.add(studentAadharNo))
                    throwExcelException(fileName, rowIndex, COL_AADHAR_STUDENT, studentAadharNo, "Duplicate Aadhar Number found in Excel");
                validateStudentAadharNoInExcel(studentListDB, studentAadharNo, fileName, rowIndex);
            }
            //non mandatory field check
            if (apaarNo != null) {
                if (!excelStudentApaarNos.add(apaarNo))
                    throwExcelException(fileName, rowIndex, COL_APAAR, apaarNo, "Duplicate Aadhar Number found in Excel");
                validateStudentApaarNoInExcel(studentListDB, apaarNo, fileName, rowIndex);
            }
            //non mandatory field check
            if (busRoute != null) {
                validateStudentBusRouteInExcel(busCircularsDB, busRoute, fileName, rowIndex);
            }

            if (isSibling.equalsIgnoreCase("no")) {
                //excel parent mand fields (with validation) :- parentUserId,parentPhoneNo     NOTE:email is not mand for parent
                //excel parent mand fields:- parentType, parentName, isActive
                //excel parent non-mand fields (with validation) :- parentEmail, parentAadharNo,

                //mandatory field check
                if (parentType == null) {
                    throwExcelException(fileName, rowIndex, COL_PARENT_TYPE, parentType, " is empty or null");
                }
                //isActive already checked in ExcelUtility method
                //parentName check not required as we are checking all fatherName , motherName, guardian later

                //calculating parentName based on parentType given
                String parentName = null;
                String parentAadharNo = null;
                String parentGender = null;
                LocalDate parentDob = null;
                if (parentType.equalsIgnoreCase("FATHER")) {
                    if (fatherName == null) {//mandatory field check
                        throwExcelException(fileName, rowIndex, COL_FATHER_NAME, fatherName, "Father Name not found in Excel");
                    }
                    parentName = fatherName;
                    parentAadharNo = fatherAadharNo;
                    parentGender = AppConstant.MALE;
                    parentDob = fatherDob;
                } else if (parentType.equalsIgnoreCase("MOTHER")) {
                    if (motherName == null) {//mandatory field check
                        throwExcelException(fileName, rowIndex, COL_MOTHER_NAME, motherName, "Mother Name not found in Excel");
                    }
                    parentName = motherName;
                    parentAadharNo = motherAadharNo;
                    parentGender = AppConstant.FEMALE;
                    parentDob = motherDob;
                } else {
                    if (guardianName == null) {//mandatory field check
                        throwExcelException(fileName, rowIndex, COL_GUARDIAN_NAME, guardianName, "Guardian Name not found in Excel");
                    }
                    parentName = guardianName;
                    parentAadharNo = guardianAadharNo;
                    parentDob = guardianDob;
                }

                //------------------------Parent------------------------------
                //mandatory field check
                if (parentPhoneNo != null) {
                    if (!excelParentPhoneNos.add(parentPhoneNo))
                        throwExcelException(fileName, rowIndex, COL_PHONE, parentPhoneNo, "Duplicate Phone Number found in Excel");
                    validateParentPhoneNoInExcel(userListDB, parentPhoneNo, fileName, rowIndex);
                } else {
                    throwExcelException(fileName, rowIndex, COL_PHONE, parentPhoneNo, "is empty or null");
                }
                //non mandatory field check
                if (parentEmail != null) {
                    parentEmail = parentEmail.toLowerCase();
                    if (!excelParentEmails.add(parentEmail))
                        throwExcelException(fileName, rowIndex, COL_EMAIL, parentEmail, "Duplicate Email found in Excel");
                    validateParentEmailInExcel(userListDB, parentEmail, fileName, rowIndex);
                }
                //non mandatory field check fatherAadharNo
                if (fatherAadharNo != null) {
                    if (!excelFatherAadharNos.add(fatherAadharNo))
                        throwExcelException(fileName, rowIndex, COL_AADHAR_FATHER, fatherAadharNo, "Duplicate Aadhar Number found in Excel");
                    validateFatherAadharNoInExcel(studentOtherInfoListDB, fatherAadharNo, fileName, rowIndex);
                }
                //non mandatory field check motherAadharNo
                if (motherAadharNo != null) {
                    if (!excelMotherAadharNos.add(motherAadharNo))
                        throwExcelException(fileName, rowIndex, COL_AADHAR_MOTHER, motherAadharNo, "Duplicate Aadhar Number found in Excel");
                    validateMotherAadharNoInExcel(studentOtherInfoListDB, motherAadharNo, fileName, rowIndex);
                }
                //non mandatory field check guardianAadharNo
                if (guardianAadharNo != null) {
                    if (!excelGuardianAadharNos.add(guardianAadharNo))
                        throwExcelException(fileName, rowIndex, COL_AADHAR_GUARDIAN, guardianAadharNo, " Duplicate Aadhar Number found in Excel");
                    validateGuardianAadharNoInExcel(studentOtherInfoListDB, guardianAadharNo, fileName, rowIndex);
                }

                //-------------User-------------
                User user = new User();
                //user.setUsername(parentUserId);
                user.setEmail(parentEmail);
                user.setPhoneNo(parentPhoneNo);
                user.setAltPhoneNo(altPhoneNo);
                user.setIsActive(isActive);
                user.setPassword(AppConstant.DEFAULT_PASSWORD);
                user.setTenantId(tenantId);
                user.setRole(AppConstant.PARENT_ROLE);
                studentParentUserDTO.setUser(user);
                //-------------Parent-------------
                Parent parent = new Parent();
                ///parent.setUsername(parentUserId);
                parent.setParentType(parentType);
                parent.setFullName(parentName);
                parent.setAadharNo(parentAadharNo);
                parent.setGender(parentGender);
                parent.setDob(parentDob);
                studentParentUserDTO.setParent(parent);

                //-------------Student-------------
                Student student = new Student();
                student.setSession(DateUtility.getCurrentAcademicSession());
                student.setUsername(admissionNo);
                //student.setParentUsername(parentUserId);
                student.setParentType(parentType);
                student.setFullName(studentFullName);
                student.setAdmissionNo(admissionNo);
                student.setAdmissionDate(admissionDate);
                student.setAadharNo(studentAadharNo);
                student.setApaarNo(apaarNo);
                student.setGender(studentGender);
                student.setClassName(className);
                student.setRollNo(rollNo);
                student.setFatherName(fatherName);
                student.setMotherName(motherName);
                student.setGuardianName(guardianName);
                student.setDob(studentDob);
                student.setMedium(medium);
                student.setPermanentAddress(permanentAddress);
                student.setCurrentAddress(currentAddress);
                student.setRuralOrUrban(ruralOrUrban);
                //student.setPhotoUrl(null);//ExcelFileUtility.getStringCellValue(row, 23)
                //String photoUrl = "https://schooltech-s3.s3.ap-south-1.amazonaws.com/" + ExcelFileUtility.getStringCellValue(row, 26) + "/images/students/" + ExcelFileUtility.getStringCellValue(row, 1).toLowerCase() + ".jpg";
                //student.setPhotoUrl(photoUrl);
                student.setBusRoute(busRoute);
                Map<String, String> parentActionsMapStudent = new HashMap<>();
                parentActionsMapStudent.put(PARENT_ACTIONS, "true");
                student.setParentActions(parentActionsMapStudent);
                Map<String, String> studentDocuments = new HashMap<>();
                student.setDocuments(studentDocuments);

                List<Student> studentList = new ArrayList<>();
                studentList.add(student);

                //-------------Student Other Info-------------
                StudentOtherInfo studentOtherInfo = new StudentOtherInfo();
                studentOtherInfo.setUsername(admissionNo);
                studentOtherInfo.setFamilyId(familyId);

                studentOtherInfo.setFatherDob(fatherDob);
                studentOtherInfo.setFatherAadharNo(fatherAadharNo);
                studentOtherInfo.setFatherOccupation(fatherOccupation);
                studentOtherInfo.setFatherEducation(fatherEducation);

                studentOtherInfo.setMotherDob(motherDob);
                studentOtherInfo.setMotherAadharNo(motherAadharNo);
                studentOtherInfo.setMotherOccupation(motherOccupation);
                studentOtherInfo.setMotherEducation(motherEducation);

                studentOtherInfo.setGuardianDob(guardianDob);
                studentOtherInfo.setGuardianAadharNo(guardianAadharNo);
                studentOtherInfo.setGuardianOccupation(guardianOccupation);
                studentOtherInfo.setGuardianEducation(guardianEducation);
                studentOtherInfo.setGuardianGender(guardianGender);

                studentOtherInfo.setReligion(religion);
                studentOtherInfo.setCaste(caste);
                studentOtherInfo.setNationality(nationality);
                studentOtherInfo.setDomicile(domicile);
                studentOtherInfo.setIsMinority(isMinority);
                studentOtherInfo.setMinorityCategory(minorityCategory);
                studentOtherInfo.setIsBPL(isBPL);
                studentOtherInfo.setStream(stream);
                studentOtherInfo.setSubjectsOpted(subjectsOpted);
                studentOtherInfo.setSubjectOptional(subjectsOptional);
                studentOtherInfo.setPreviousSchoolName(previousSchoolName);
                studentOtherInfo.setPreviousAdmissionNo(previousAdmissionNo);
                studentOtherInfo.setPreviousSchoolCode(previousSchoolCode);
                studentOtherInfo.setPreviousLeavingDate(previousLeavingDate);
                studentOtherInfo.setPreviousClassPassed(previousClassPassed);
                studentOtherInfo.setPreviousMarksObtained(previousMarksObtained);
//                Map<String, String> parentActionsMap = new HashMap<>();
//                //mapStudentInfo.put(PARENT_ACTIONS, "false");
//                studentOtherInfo.setParentActions(parentActionsMap);

                List<StudentOtherInfo> studentOtherInfoList = new ArrayList<>();
                studentOtherInfoList.add(studentOtherInfo);

                studentParentUserDTO.setStudentList(studentList);
                studentParentUserDTO.setStudentOtherInfoList(studentOtherInfoList);
                studentParentUserDTOList.add(studentParentUserDTO);

            } else if (isSibling.equalsIgnoreCase("yes")) {

                //mandatory checks in case of isSibling "yes"
                if (parentType == null) {
                    throwExcelException(fileName, rowIndex, COL_PARENT_TYPE, parentType, " is empty or null");
                }
                if (parentPhoneNo == null) {
                    throwExcelException(fileName, rowIndex, COL_PHONE, parentPhoneNo, " is empty or null");
                }


                if (!studentParentUserDTOList.isEmpty()) {
                    StudentParentUserDTO lastStudentParentUserDTO = studentParentUserDTOList.get(studentParentUserDTOList.size() - 1);
                    User siblingParentUserData = lastStudentParentUserDTO.getUser();
                    Parent siblingParent = lastStudentParentUserDTO.getParent();
                    List<Student> studentList = lastStudentParentUserDTO.getStudentList();
                    List<StudentOtherInfo> studentOtherInfoList = lastStudentParentUserDTO.getStudentOtherInfoList();


                    if (parentPhoneNo.equalsIgnoreCase(siblingParentUserData.getPhoneNo())) {
                        //mandatory checks in case of isSibling "yes"
                        if (!parentType.equalsIgnoreCase(siblingParent.getParentType())) {
                            throwExcelException(fileName, rowIndex, COL_PARENT_TYPE, parentType, "PARENT TYPE should be same as" + siblingParent.getParentType());
                        }
                        //non mandatory check in case of isSibling "yes"
                        if (parentEmail != null && !parentEmail.equalsIgnoreCase(siblingParentUserData.getEmail())) {
                            throwExcelException(fileName, rowIndex, COL_PARENT_EMAIL, parentEmail, "Email should be same as " + siblingParentUserData.getEmail());
                        }
                        Student siblingStudent = studentList.get(studentList.size() - 1);
                        StudentOtherInfo siblingStudentInfo = studentOtherInfoList.get(studentOtherInfoList.size() - 1);
                        //-------------Student-------------
                        Student student = new Student();
                        student.setUsername(admissionNo);
                        if (siblingParent.getUsername() != null)
                            student.setParentUsername(siblingParent.getUsername());
                        student.setSession(DateUtility.getCurrentAcademicSession());
                        student.setParentType(siblingParent.getParentType());
                        student.setFullName(studentFullName);
                        student.setAdmissionNo(admissionNo);
                        student.setAdmissionDate(admissionDate);
                        student.setAadharNo(studentAadharNo);
                        student.setApaarNo(apaarNo);
                        student.setGender(studentGender);
                        student.setClassName(className);
                        student.setRollNo(rollNo);
                        student.setFatherName(siblingStudent.getFatherName());
                        student.setMotherName(siblingStudent.getMotherName());
                        student.setGuardianName(siblingStudent.getGuardianName());
                        student.setDob(studentDob);
                        student.setMedium(medium);
                        student.setPermanentAddress(permanentAddress);
                        student.setCurrentAddress(currentAddress);
                        student.setRuralOrUrban(ruralOrUrban);
                        //student.setPhotoUrl(null);//ExcelFileUtility.getStringCellValue(row, 23)
                        //String photoUrl = "https://schooltech-s3.s3.ap-south-1.amazonaws.com/" + ExcelFileUtility.getStringCellValue(row, 26) + "/images/students/" + ExcelFileUtility.getStringCellValue(row, 1).toLowerCase() + ".jpg";
                        //student.setPhotoUrl(photoUrl);
                        student.setBusRoute(busRoute);
                        Map<String, String> parentActionsMapStudent = new HashMap<>();
                        parentActionsMapStudent.put(PARENT_ACTIONS, "true");
                        student.setParentActions(parentActionsMapStudent);
                        Map<String, String> studentDocuments = new HashMap<>();
                        student.setDocuments(studentDocuments);

                        studentList.add(student);

                        //-------------Student Other Info-------------
                        StudentOtherInfo studentOtherInfo = new StudentOtherInfo();
                        studentOtherInfo.setUsername(admissionNo);
                        studentOtherInfo.setFamilyId(familyId);

                        studentOtherInfo.setFatherDob(siblingStudentInfo.getFatherDob());
                        studentOtherInfo.setFatherAadharNo(siblingStudentInfo.getFatherAadharNo());
                        studentOtherInfo.setFatherOccupation(siblingStudentInfo.getFatherOccupation());
                        studentOtherInfo.setFatherEducation(siblingStudentInfo.getFatherEducation());

                        studentOtherInfo.setMotherDob(siblingStudentInfo.getMotherDob());
                        studentOtherInfo.setMotherAadharNo(siblingStudentInfo.getMotherAadharNo());
                        studentOtherInfo.setMotherOccupation(siblingStudentInfo.getMotherOccupation());
                        studentOtherInfo.setMotherEducation(siblingStudentInfo.getMotherEducation());

                        studentOtherInfo.setGuardianDob(siblingStudentInfo.getGuardianDob());
                        studentOtherInfo.setGuardianAadharNo(siblingStudentInfo.getGuardianAadharNo());
                        studentOtherInfo.setGuardianOccupation(siblingStudentInfo.getGuardianOccupation());
                        studentOtherInfo.setGuardianEducation(siblingStudentInfo.getGuardianEducation());
                        studentOtherInfo.setGuardianGender(siblingStudentInfo.getGuardianGender());

                        studentOtherInfo.setReligion(religion);
                        studentOtherInfo.setCaste(caste);
                        studentOtherInfo.setNationality(nationality);
                        studentOtherInfo.setDomicile(domicile);
                        studentOtherInfo.setIsMinority(isMinority);
                        studentOtherInfo.setMinorityCategory(minorityCategory);
                        studentOtherInfo.setIsBPL(isBPL);
                        studentOtherInfo.setStream(stream);
                        studentOtherInfo.setSubjectsOpted(subjectsOpted);
                        studentOtherInfo.setSubjectOptional(subjectsOptional);
                        studentOtherInfo.setPreviousSchoolName(previousSchoolName);
                        studentOtherInfo.setPreviousAdmissionNo(previousAdmissionNo);
                        studentOtherInfo.setPreviousSchoolCode(previousSchoolCode);
                        studentOtherInfo.setPreviousLeavingDate(previousLeavingDate);
                        studentOtherInfo.setPreviousClassPassed(previousClassPassed);
                        studentOtherInfo.setPreviousMarksObtained(previousMarksObtained);
//                        Map<String, String> parentActionsMap = new HashMap<>();
//                        //mapStudentInfo.put(PARENT_ACTIONS, "false");
//                        studentOtherInfo.setParentActions(parentActionsMap);

                        studentOtherInfoList.add(studentOtherInfo);
                    } else {
                        processStudentSiblingOfExistingParent
                                (studentParentUserDTO, parentPhoneNo, parentType, fileName, rowIndex, parentEmail, admissionNo, studentFullName, admissionDate, studentAadharNo
                                        , apaarNo, className, studentGender, rollNo, studentDob, medium, permanentAddress, currentAddress,
                                        ruralOrUrban, religion, busRoute, familyId,
                                        caste, nationality, domicile, isMinority, minorityCategory, isBPL, stream, subjectsOpted, subjectsOptional, previousAdmissionNo, previousSchoolName,
                                        previousSchoolCode, previousLeavingDate, previousClassPassed, previousMarksObtained
                                );
                        studentParentUserDTOList.add(studentParentUserDTO);
                    }
                } else {
                    processStudentSiblingOfExistingParent
                            (studentParentUserDTO, parentPhoneNo, parentType, fileName, rowIndex, parentEmail, admissionNo, studentFullName, admissionDate, studentAadharNo
                                    , apaarNo, className, studentGender, rollNo, studentDob, medium, permanentAddress, currentAddress,
                                    ruralOrUrban, religion, busRoute, familyId,
                                    caste, nationality, domicile, isMinority, minorityCategory, isBPL, stream, subjectsOpted, subjectsOptional, previousAdmissionNo, previousSchoolName,
                                    previousSchoolCode, previousLeavingDate, previousClassPassed, previousMarksObtained

                            );
                    studentParentUserDTOList.add(studentParentUserDTO);
                }

            }
            //----------------------------
            rowIndex++;
        }

        studentService.saveStudentWithAssociatedEntity(studentParentUserDTOList);
    }

    public void processStudentSiblingOfExistingParent(StudentParentUserDTO studentParentUserDTO, String parentPhoneNo, String parentType, String fileName, int rowIndex, String parentEmail, String admissionNo, String studentFullName, LocalDate admissionDate, String studentAadharNo
            , String apaarNo, String className, String studentGender, Long rollNo, LocalDate studentDob, String medium, String permanentAddress, String currentAddress,
                                                      String ruralOrUrban, String religion, String busRoute, String familyId,
                                                      String caste, String nationality, String domicile, String isMinority, String minorityCategory, String isBPL, String stream, String subjectsOpted, String subjectsOptional, String previousAdmissionNo, String previousSchoolName,
                                                      String previousSchoolCode, String previousLeavingDate, String previousClassPassed, String previousMarksObtained

    ) {

        User user = authService.getUserByPhoneNo(parentPhoneNo);
        Parent siblingParentDB = parentService.getParentByUsername(user.getUsername());
        Student siblingStudent = studentService.getStudentsByParent(siblingParentDB.getUsername()).getBody().get(0);//if parent found at least a student will exist
        StudentOtherInfo siblingStudentInfo = studentService.getStudentsInfo(siblingStudent.getUsername()).getBody();
        studentParentUserDTO.setUser(user);
        studentParentUserDTO.setParent(siblingParentDB);

        //mandatory checks in case of isSibling "yes"
        if (!parentType.equalsIgnoreCase(siblingParentDB.getParentType())) {
            throwExcelException(fileName, rowIndex, COL_PARENT_TYPE, parentType, "PARENT TYPE  should be same as" + siblingParentDB.getParentType());
        }
        //non mandatory check in case of isSibling "yes"
        if (parentEmail != null && !parentEmail.equalsIgnoreCase(user.getEmail())) {
            throwExcelException(fileName, rowIndex, COL_PARENT_EMAIL, parentEmail, "Email should be same as " + user.getEmail());
        }

        //-------------Student-------------
        Student student = new Student();
        student.setUsername(admissionNo);
        student.setParentUsername(siblingParentDB.getUsername());
        student.setSession(DateUtility.getCurrentAcademicSession());
        student.setParentType(siblingParentDB.getParentType());
        student.setFullName(studentFullName);
        student.setAdmissionNo(admissionNo);
        student.setAdmissionDate(admissionDate);
        student.setAadharNo(studentAadharNo);
        student.setApaarNo(apaarNo);
        student.setGender(studentGender);
        student.setClassName(className);
        student.setRollNo(rollNo);
        student.setFatherName(siblingStudent.getFatherName());
        student.setMotherName(siblingStudent.getMotherName());
        student.setGuardianName(siblingStudent.getGuardianName());
        student.setDob(studentDob);
        student.setMedium(medium);
        student.setPermanentAddress(permanentAddress);
        student.setCurrentAddress(currentAddress);
        student.setRuralOrUrban(ruralOrUrban);
        //student.setPhotoUrl(null);//ExcelFileUtility.getStringCellValue(row, 23)
        //String photoUrl = "https://schooltech-s3.s3.ap-south-1.amazonaws.com/" + ExcelFileUtility.getStringCellValue(row, 26) + "/images/students/" + ExcelFileUtility.getStringCellValue(row, 1).toLowerCase() + ".jpg";
        //student.setPhotoUrl(photoUrl);
        student.setBusRoute(busRoute);
        Map<String, String> parentActionsMapStudent = new HashMap<>();
        parentActionsMapStudent.put(PARENT_ACTIONS, "true");
        student.setParentActions(parentActionsMapStudent);
        Map<String, String> studentDocuments = new HashMap<>();
        student.setDocuments(studentDocuments);

        List<Student> studentListForDB = new ArrayList<>();
        studentListForDB.add(student);
        studentParentUserDTO.setStudentList(studentListForDB);

        //-------------Student Other Info-------------
        StudentOtherInfo studentOtherInfo = new StudentOtherInfo();
        studentOtherInfo.setUsername(admissionNo);
        studentOtherInfo.setFamilyId(familyId);

        studentOtherInfo.setFatherDob(siblingStudentInfo.getFatherDob());
        studentOtherInfo.setFatherAadharNo(siblingStudentInfo.getFatherAadharNo());
        studentOtherInfo.setFatherOccupation(siblingStudentInfo.getFatherOccupation());
        studentOtherInfo.setFatherEducation(siblingStudentInfo.getFatherEducation());

        studentOtherInfo.setMotherDob(siblingStudentInfo.getMotherDob());
        studentOtherInfo.setMotherAadharNo(siblingStudentInfo.getMotherAadharNo());
        studentOtherInfo.setMotherOccupation(siblingStudentInfo.getMotherOccupation());
        studentOtherInfo.setMotherEducation(siblingStudentInfo.getMotherEducation());

        studentOtherInfo.setGuardianDob(siblingStudentInfo.getGuardianDob());
        studentOtherInfo.setGuardianAadharNo(siblingStudentInfo.getGuardianAadharNo());
        studentOtherInfo.setGuardianOccupation(siblingStudentInfo.getGuardianOccupation());
        studentOtherInfo.setGuardianEducation(siblingStudentInfo.getGuardianEducation());
        studentOtherInfo.setGuardianGender(siblingStudentInfo.getGuardianGender());

        studentOtherInfo.setReligion(religion);
        studentOtherInfo.setCaste(caste);
        studentOtherInfo.setNationality(nationality);
        studentOtherInfo.setDomicile(domicile);
        studentOtherInfo.setIsMinority(isMinority);
        studentOtherInfo.setMinorityCategory(minorityCategory);
        studentOtherInfo.setIsBPL(isBPL);
        studentOtherInfo.setStream(stream);
        studentOtherInfo.setSubjectsOpted(subjectsOpted);
        studentOtherInfo.setSubjectOptional(subjectsOptional);
        studentOtherInfo.setPreviousSchoolName(previousSchoolName);
        studentOtherInfo.setPreviousAdmissionNo(previousAdmissionNo);
        studentOtherInfo.setPreviousSchoolCode(previousSchoolCode);
        studentOtherInfo.setPreviousLeavingDate(previousLeavingDate);
        studentOtherInfo.setPreviousClassPassed(previousClassPassed);
        studentOtherInfo.setPreviousMarksObtained(previousMarksObtained);
//        Map<String, String> parentActionsMap = new HashMap<>();
//        //mapStudentInfo.put(PARENT_ACTIONS, "false");
//        studentOtherInfo.setParentActions(parentActionsMap);

        List<StudentOtherInfo> studentOtherInfoListForDB = new ArrayList<>();
        studentOtherInfoListForDB.add(studentOtherInfo);
        studentParentUserDTO.setStudentOtherInfoList(studentOtherInfoListForDB);

    }


    private void validateParentPhoneNoInExcel(List<User> userListDB, String phoneNo, String excelFileName, int rowIndex) {
        Set<String> phoneNosInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getPhoneNo() != null && !u.getPhoneNo().isBlank())
                        .map(u -> u.getPhoneNo().trim().toLowerCase())
                        .collect(Collectors.toSet());

        if (phoneNosInUserListDB.contains(phoneNo)) {
            throwExcelException(excelFileName, rowIndex, COL_PHONE, phoneNo, "already exists in Parent User Data");
        }
    }

    private void validateParentEmailInExcel(List<User> userListDB, String email, String excelFileName, int rowIndex) {
        Set<String> emailsInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getEmail() != null && !u.getEmail().isBlank())
                        .map(u -> u.getEmail().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (emailsInUserListDB.contains(email)) {
            throwExcelException(excelFileName, rowIndex, COL_EMAIL, email, "already exists in Parent User Data");
        }
    }

    //STudent
    private void validateStudentAadharNoInExcel(List<Student> studentListDB, String aadharNo, String excelFileName, int rowIndex) {
        Set<String> aadharNoInStudentListDB =
                studentListDB.stream()
                        .filter(s -> s != null && s.getAadharNo() != null && !s.getAadharNo().isBlank())
                        .map(s -> s.getAadharNo().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (aadharNoInStudentListDB.contains(aadharNo)) {
            throwExcelException(excelFileName, rowIndex, COL_AADHAR_STUDENT, aadharNo, "already exists in Student Data");
        }
    }

    private void validateStudentApaarNoInExcel(List<Student> studentListDB, String apaarNo, String excelFileName, int rowIndex) {
        Set<String> apaarNoInStudentListDB =
                studentListDB.stream()
                        .filter(s -> s != null && s.getApaarNo() != null && !s.getApaarNo().isBlank())
                        .map(s -> s.getApaarNo().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (apaarNoInStudentListDB.contains(apaarNo)) {
            throwExcelException(excelFileName, rowIndex, COL_APAAR, apaarNo, "already exists in Student Data");
        }
    }

    private void validateStudentAdmissionNoInExcel(List<Student> studentListDB, String admissionNo, String excelFileName, int rowIndex) {
        Set<String> admissionNoInStudentListDB =
                studentListDB.stream()
                        .filter(s -> s != null && s.getAdmissionNo() != null && !s.getAdmissionNo().isBlank())
                        .map(s -> s.getAdmissionNo().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (admissionNoInStudentListDB.contains(admissionNo)) {
            throwExcelException(excelFileName, rowIndex, COL_ADMISSION_NO, admissionNo, "already exists in Student Data");
        }
    }


    private void validateFatherAadharNoInExcel(List<StudentOtherInfo> studentOtherInfoListDB, String fatherAadharNo, String excelFileName, int rowIndex) {
        Set<String> aadharNoInStudentOtherInfoListDB =
                studentOtherInfoListDB.stream()
                        .filter(p -> p != null && p.getFatherAadharNo() != null && !p.getFatherAadharNo().isBlank())
                        .map(p -> p.getFatherAadharNo().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (aadharNoInStudentOtherInfoListDB.contains(fatherAadharNo)) {
            throwExcelException(excelFileName, rowIndex, COL_AADHAR_FATHER, fatherAadharNo, "already exists in Student Info Data");
        }
    }

    private void validateMotherAadharNoInExcel(List<StudentOtherInfo> studentOtherInfoListDB, String motherAadharNo, String excelFileName, int rowIndex) {
        Set<String> aadharNoInStudentOtherInfoListDB =
                studentOtherInfoListDB.stream()
                        .filter(p -> p != null && p.getMotherAadharNo() != null && !p.getMotherAadharNo().isBlank())
                        .map(p -> p.getMotherAadharNo().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (aadharNoInStudentOtherInfoListDB.contains(motherAadharNo)) {
            throwExcelException(excelFileName, rowIndex, COL_AADHAR_FATHER, motherAadharNo, "already exists in Student Info Data");
        }
    }

    private void validateGuardianAadharNoInExcel(List<StudentOtherInfo> studentOtherInfoListDB, String guardianAadharNo, String excelFileName, int rowIndex) {
        Set<String> aadharNoInStudentOtherInfoListDB =
                studentOtherInfoListDB.stream()
                        .filter(p -> p != null && p.getGuardianAadharNo() != null && !p.getGuardianAadharNo().isBlank())
                        .map(p -> p.getGuardianAadharNo().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (aadharNoInStudentOtherInfoListDB.contains(guardianAadharNo)) {
            throwExcelException(excelFileName, rowIndex, COL_AADHAR_FATHER, guardianAadharNo, "already exists in Student Info Data");
        }
    }

//    private void validateClassNamePresentInClassCircularInExcel(List<ClassCircular> classCircularsDB, String className, String excelFileName) {
//        Set<String> classCirculars =
//                classCircularsDB.stream()
//                        .filter(t -> t != null && t.getClassName() != null && !t.getClassName().isBlank())
//                        .map(t -> t.getClassName().trim().toLowerCase())
//                        .collect(Collectors.toSet());
//        if (!classCirculars.contains(className.toLowerCase())) {
//            throwGenericExcelException(excelFileName, "Class Name", className, "not present in class circular data");
//        }
//    }

    private void validateStudentBusRouteInExcel(List<BusCircular> busCircularsDB, String busRoute, String excelFileName, int rowIndex) {
        Set<String> busRoutesInDB =
                busCircularsDB.stream()
                        .filter(t -> t != null && t.getBusRoute() != null && !t.getBusRoute().isBlank())
                        .map(t -> t.getBusRoute().trim().toLowerCase())
                        .collect(Collectors.toSet());
        if (!busRoutesInDB.contains(busRoute.toLowerCase())) {
            throwExcelException(excelFileName, rowIndex, COL_BUS_ROUTE, busRoute, "not present in bus data");
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

    private void throwGenericExcelException(
            String fileName,
            String attribute,
            String value,
            String message
    ) {
        throw new RuntimeException(
                "OPERATION ABORTED: " + fileName +
                        " | Attribute \"" + attribute + "\"" +
                        " | Value: \"" + value + "\"" +
                        " | Reason: " + message
        );
    }


}