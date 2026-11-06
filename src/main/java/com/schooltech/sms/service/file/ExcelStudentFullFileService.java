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
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.student.Parent;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.student.StudentOtherInfo;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.service.student.ParentService;
import com.schooltech.sms.service.student.StudentFeeService;
import com.schooltech.sms.service.student.StudentService;
import com.schooltech.sms.utility.AppUtility;
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
import static com.schooltech.sms.constant.AppConstant.PARENT_TYPE_LIST;

@Service
public class ExcelStudentFullFileService {
    private static final Logger log = LoggerFactory.getLogger(ExcelStudentFullFileService.class);
    private static final String COL_ADD = "Add";
    private static final String COL_IS_ACTIVE = "is Active";
    private static final String COL_ADMISSION_NO = "Admission No";
    private static final String COL_STUDENT_FULL_NAME = "Student Full Name";
    private static final String COL_IS_SIBLING = "Is Sibling";
    private static final String COL_PARENT_TYPE = "Parent Type";
    private static final String COL_PARENT_NAME = "Father Name";
    private static final String COL_PHONE = "Phone No";
    private static final String COL_PARENT_EMAIL = "Email";

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

    /**
     * Holds the newer, non-mandatory Excel columns (DOB, admission date, roll no,
     * gender, and explicit father/mother/guardian name overrides) so they don't
     * need to be threaded through every method as separate parameters.
     */
    private record StudentExtraFields(
            LocalDate dob,
            LocalDate admissionDate,
            Long rollNo,
            String gender,
            String fatherNameOverride,
            String motherNameOverride,
            String guardianNameOverride
    ) {
        /** Returns fatherName, overridden by the explicit Excel column if present. */
        String overrideFatherName(String derivedOrInheritedFatherName) {
            return fatherNameOverride != null ? fatherNameOverride : derivedOrInheritedFatherName;
        }

        /** Returns motherName, overridden by the explicit Excel column if present. */
        String overrideMotherName(String derivedOrInheritedMotherName) {
            return motherNameOverride != null ? motherNameOverride : derivedOrInheritedMotherName;
        }

        /** Returns guardianName, overridden by the explicit Excel column if present. */
        String overrideGuardianName(String derivedOrInheritedGuardianName) {
            return guardianNameOverride != null ? guardianNameOverride : derivedOrInheritedGuardianName;
        }
    }

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
        List<StudentParentUserDTO> studentParentUserDTOList = new ArrayList<>();
        //Existing DB data
        List<User> userListDB = userRepository.findAll();
        List<Student> studentListDB = studentRepository.findAll();
        List<ClassCircular> classCircularsDB = classCircularRepository.findBySession(DateUtility.getCurrentAcademicSession());

        //Parent
        Set<String> excelParentEmails = new HashSet<>();
        Set<String> excelParentPhoneNos = new HashSet<>();
        //Student
        Set<String> excelStudentAdmissionNos = new HashSet<>();

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
                rowIndex++;
                continue;
            }

            StudentParentUserDTO studentParentUserDTO = new StudentParentUserDTO();

            String className = fileName.split("_")[2].replace(".xlsx", "").toLowerCase(); //class Name is coming from excel file name
            validateClassNamePresentInClassCircularInExcel(classCircularsDB, className, fileName, rowIndex);

            int columnIndex = 0;
            Boolean isActive = ExcelFileUtility.parseBooleanNew(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName), fileName, rowIndex, COL_IS_ACTIVE);
            String admissionNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String studentFullName = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String isSibling = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String parentType = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String parentNameFromExcel = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String parentPhoneNo = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String parentEmail = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            //Temporary Fields- please use else comment whatever needed as per school
            String admissionStatusFromExcel = Objects.requireNonNull(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName)).toUpperCase();
            String dobRaw = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate dob = dobRaw != null
                    ? ExcelFileUtility.parseDateDDhyphenMMhyphenYY(dobRaw)
                    : null;
            String admissionDateRaw = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            LocalDate admissionDate = admissionDateRaw != null
                    ? ExcelFileUtility.parseDateDDhyphenMMhyphenYY(admissionDateRaw)
                    : null;

            String rollNoRaw = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            Long rollNo = rollNoRaw != null
                    ? ExcelFileUtility.parseLong(rollNoRaw)
                    : null;

            String studentGenderFromExcel = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String fatherNameFromExcelExplicit = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String motherNameFromExcelExplicit = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String guardianNameFromExcelExplicit = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName); // Date of Submission — intentionally ignored
            String addressFromExcel = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName); // Current Address


            log.info("SchoolTech.... Processed " + columnIndex + " fields in file: " + fileName);

            StudentExtraFields extraFields = new StudentExtraFields(
                    dob, admissionDate, rollNo, studentGenderFromExcel,
                    fatherNameFromExcelExplicit, motherNameFromExcelExplicit, guardianNameFromExcelExplicit
            );

            //className is validated at front end side
            //isActive checked inside parseBooleanNew method

            //mandatory field check
            if (studentFullName == null) {
                throwExcelException(fileName, rowIndex, COL_STUDENT_FULL_NAME, studentFullName, " is empty or null");
            }
            //mandatory field check
            if (isSibling == null) {
                throwExcelException(fileName, rowIndex, COL_IS_SIBLING, isSibling, " is empty or null");
            }
            //mandatory field check
            if (parentType == null) {
                throwExcelException(fileName, rowIndex, COL_PARENT_TYPE, parentType, " is empty or null");
            }
            if (!PARENT_TYPE_LIST.contains(parentType)) {
                throwExcelException(fileName, rowIndex, COL_PARENT_TYPE, parentType, "Parent Type allowed values are: " + PARENT_TYPE_LIST);
            }
            //mandatory field check
            if (parentNameFromExcel == null) {
                throwExcelException(fileName, rowIndex, COL_PARENT_NAME, parentNameFromExcel, "Father Name not found in Excel");
            }
            //mandatory field check
            if (parentPhoneNo == null) {
                throwExcelException(fileName, rowIndex, COL_PHONE, parentPhoneNo, "is empty or null");
            }
            //validating parentPhoneNo is valid or not
            if (!AppUtility.isValidPhoneNumber(parentPhoneNo)) {
                throwExcelException(fileName, rowIndex, COL_PHONE, parentPhoneNo, "Phone Number must be of 10 digits & only numbers");
            }
            //mandatory field check -Duplicate in Excel & DB
            if (admissionNo != null) {
                if (!excelStudentAdmissionNos.add(admissionNo))
                    throwExcelException(fileName, rowIndex, COL_ADMISSION_NO, admissionNo, "Duplicate Admission No found in Excel");
                validateStudentAdmissionNoInExcel(studentListDB, admissionNo, fileName, rowIndex);
            }
            //validating parentEmail is valid or not
            if (parentEmail != null) {
                if (!AppUtility.isValidEmail(parentEmail)) {
                    throwExcelException(fileName, rowIndex, COL_PARENT_EMAIL, parentEmail, "Email is Invalid");
                }
            }

            if (isSibling.equalsIgnoreCase("no")) {
                //isActive already checked in ExcelUtility method

                //calculating parentName based on parentType given
                String parentName = parentNameFromExcel;

                String fatherName = null;
                String motherName = null;
                String guardianName = null;
                String parentGender = null;

                if (parentType.equalsIgnoreCase("FATHER")) {
                    fatherName = parentName;
                    parentGender = AppConstant.MALE;
                } else if (parentType.equalsIgnoreCase("MOTHER")) {
                    motherName = parentName;
                    parentGender = AppConstant.FEMALE;
                } else {
                    guardianName = parentName;
                    //Guardian Gender should be updated from UI App
                }

                // Explicit Excel columns override the derived values, if provided
                fatherName = extraFields.overrideFatherName(fatherName);
                motherName = extraFields.overrideMotherName(motherName);
                guardianName = extraFields.overrideGuardianName(guardianName);

                //------------------------Parent------------------------------
                //mandatory field check -Duplicate in Excel & DB
                if (!excelParentPhoneNos.add(parentPhoneNo))
                    throwExcelException(fileName, rowIndex, COL_PHONE, parentPhoneNo, "Duplicate Phone Number found in Excel");
                validateParentPhoneNoInExcel(userListDB, parentPhoneNo, fileName, rowIndex);

                //non mandatory field check -Duplicate in Excel & DB
                if (parentEmail != null) {
                    if (!excelParentEmails.add(parentEmail))
                        throwExcelException(fileName, rowIndex, COL_PARENT_EMAIL, parentEmail, "Duplicate Email found in Excel");
                    validateParentEmailInExcel(userListDB, parentEmail, fileName, rowIndex);
                }
                //-------------User-------------
                User user = new User();
                //user.setUsername(parentUserId);
                user.setEmail(parentEmail);
                user.setPhoneNo(parentPhoneNo);
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
                parent.setGender(parentGender);
                Map<String, String> parentDocuments = new HashMap<>();
                parent.setDocuments(parentDocuments);
                studentParentUserDTO.setParent(parent);
                //-------------Student-------------
                Student student = new Student();
                //student.setUsername(admissionNo);
                //student.setRollNo(rollNo);
                student.setSession(DateUtility.getCurrentAcademicSession());
                student.setParentType(parentType);
                student.setFullName(studentFullName);
                student.setAdmissionNo(admissionNo);
                student.setClassName(className);
                student.setFatherName(fatherName);
                student.setMotherName(motherName);
                student.setGuardianName(guardianName);
                student.setDob(extraFields.dob());
                student.setAdmissionDate(extraFields.admissionDate());
                student.setRollNo(extraFields.rollNo());
                student.setGender(extraFields.gender());
                //ParentActions
                Map<String, String> parentActionsMapStudent = new HashMap<>();
                parentActionsMapStudent.put(PARENT_ACTIONS, "true");
                student.setParentActions(parentActionsMapStudent);
                //Student Documents
                Map<String, String> studentDocuments = new HashMap<>();
                student.setDocuments(studentDocuments);
                List<Student> studentList = new ArrayList<>();
                studentList.add(student);
                studentParentUserDTO.setStudentList(studentList);
                //Temporary Fields- please use else comment whatever needed as per school
                student.setAdmissionStatus(Student.AdmissionStatus.valueOf(admissionStatusFromExcel));
                student.setPermanentAddress(addressFromExcel);
                student.setCurrentAddress(addressFromExcel);
                //-------------Student Other Info-------------
                StudentOtherInfo studentOtherInfo = new StudentOtherInfo();
                //studentOtherInfo.setUsername(admissionNo);
                List<StudentOtherInfo> studentOtherInfoList = new ArrayList<>();
                studentOtherInfoList.add(studentOtherInfo);
                studentParentUserDTO.setStudentOtherInfoList(studentOtherInfoList);

                studentParentUserDTOList.add(studentParentUserDTO);

            } else if (isSibling.equalsIgnoreCase("yes")) {

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
                        if (!parentNameFromExcel.equalsIgnoreCase(siblingParent.getFullName())) {
                            throwExcelException(fileName, rowIndex, COL_PARENT_NAME, parentNameFromExcel, "Parent Name should be same as " + siblingParent.getFullName());
                        }
                        Student siblingStudent = studentList.get(studentList.size() - 1);
                        StudentOtherInfo siblingStudentInfo = studentOtherInfoList.get(studentOtherInfoList.size() - 1);
                        //-------------Student-------------
                        Student student = new Student();
                        //student.setUsername(admissionNo);
                        //student.setRollNo(rollNo);
                        if (siblingParent.getUsername() != null) {
                            //this case will arise when there is a sibling at first row and next one also the sibling with same parent
                            //hence setting the student parentusername for 2nd or more coz the first one already has that info
                            student.setParentUsername(siblingParent.getUsername());
                        }
                        student.setSession(DateUtility.getCurrentAcademicSession());
                        student.setParentType(siblingParent.getParentType());
                        student.setFullName(studentFullName);
                        student.setAdmissionNo(admissionNo);
                        student.setClassName(className);
                        student.setFatherName(extraFields.overrideFatherName(siblingStudent.getFatherName()));
                        student.setMotherName(extraFields.overrideMotherName(siblingStudent.getMotherName()));
                        student.setGuardianName(extraFields.overrideGuardianName(siblingStudent.getGuardianName()));
                        student.setPermanentAddress(siblingStudent.getPermanentAddress());
                        student.setCurrentAddress(siblingStudent.getCurrentAddress());
                        student.setDob(extraFields.dob());
                        student.setAdmissionDate(extraFields.admissionDate());
                        student.setRollNo(extraFields.rollNo());
                        student.setGender(extraFields.gender());
                        //ParentActions
                        Map<String, String> parentActionsMapStudent = new HashMap<>();
                        parentActionsMapStudent.put(PARENT_ACTIONS, "true");
                        student.setParentActions(parentActionsMapStudent);
                        //Student documents
                        Map<String, String> studentDocuments = new HashMap<>();
                        student.setDocuments(studentDocuments);

                        //Temporary Fields- please use else comment whatever needed as per school
                        student.setAdmissionStatus(Student.AdmissionStatus.valueOf(admissionStatusFromExcel));
                        student.setPermanentAddress(siblingStudent.getPermanentAddress());
                        student.setCurrentAddress(siblingStudent.getCurrentAddress());

                        studentList.add(student);
                        lastStudentParentUserDTO.setStudentList(studentList);
                        //studentParentUserDTO.setStudentList(studentList);
                        //-------------Student Other Info-------------
                        StudentOtherInfo studentOtherInfo = new StudentOtherInfo();
                        //studentOtherInfo.setUsername(admissionNo);
                        studentOtherInfo.setFamilyId(siblingStudentInfo.getFamilyId());
                        //Father
                        studentOtherInfo.setFatherDob(siblingStudentInfo.getFatherDob());
                        studentOtherInfo.setFatherAadharNo(siblingStudentInfo.getFatherAadharNo());
                        studentOtherInfo.setFatherOccupation(siblingStudentInfo.getFatherOccupation());
                        studentOtherInfo.setFatherEducation(siblingStudentInfo.getFatherEducation());
                        //Mother
                        studentOtherInfo.setMotherDob(siblingStudentInfo.getMotherDob());
                        studentOtherInfo.setMotherAadharNo(siblingStudentInfo.getMotherAadharNo());
                        studentOtherInfo.setMotherOccupation(siblingStudentInfo.getMotherOccupation());
                        studentOtherInfo.setMotherEducation(siblingStudentInfo.getMotherEducation());
                        //Guardian
                        studentOtherInfo.setGuardianDob(siblingStudentInfo.getGuardianDob());
                        studentOtherInfo.setGuardianAadharNo(siblingStudentInfo.getGuardianAadharNo());
                        studentOtherInfo.setGuardianOccupation(siblingStudentInfo.getGuardianOccupation());
                        studentOtherInfo.setGuardianEducation(siblingStudentInfo.getGuardianEducation());
                        studentOtherInfo.setGuardianGender(siblingStudentInfo.getGuardianGender());
                        studentOtherInfoList.add(studentOtherInfo);
                        lastStudentParentUserDTO.setStudentOtherInfoList(studentOtherInfoList);

                        //studentParentUserDTOList.add(studentParentUserDTO);
                    } else {
                        processStudentSiblingOfExistingParent(studentParentUserDTO, parentNameFromExcel, parentPhoneNo, parentType, fileName, rowIndex, parentEmail, admissionNo, studentFullName, className, admissionStatusFromExcel, extraFields);
                        studentParentUserDTOList.add(studentParentUserDTO);
                    }
                } else {
                    processStudentSiblingOfExistingParent(studentParentUserDTO, parentNameFromExcel, parentPhoneNo, parentType, fileName, rowIndex, parentEmail, admissionNo, studentFullName, className, admissionStatusFromExcel, extraFields);
                    studentParentUserDTOList.add(studentParentUserDTO);
                }
            }
            //----------------------------
            rowIndex++;
        }
        studentService.saveStudentWithAssociatedEntity(studentParentUserDTOList);
    }

    public void processStudentSiblingOfExistingParent(StudentParentUserDTO studentParentUserDTO, String parentNameFromExcel, String parentPhoneNo, String parentType, String fileName, int rowIndex, String parentEmail, String admissionNo, String studentFullName, String className, String admissionStatusFromExcel, StudentExtraFields extraFields) {
        User user = authService.getUserByPhoneNo(parentPhoneNo);
        if (user == null) {
            throwExcelException(fileName, rowIndex, COL_PHONE, parentPhoneNo, "PARENT Phone No not found. Please make it is Sibling \"NO\"");
        }
        Parent siblingParentDB = parentService.getParentByUsername(user.getUsername());
        List<Student> siblings = studentService.getStudentsByParent(siblingParentDB.getUsername()).getBody();
        if (siblings != null && siblings.isEmpty())
            throwGenericExcelException(fileName, "SIBLINGS", "", "Siblings not found");
        Student siblingStudent = siblings.get(0);//if parent found at least a student will exist
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
        if (parentNameFromExcel != null && !parentNameFromExcel.equalsIgnoreCase(siblingParentDB.getFullName())) {
            throwExcelException(fileName, rowIndex, COL_PARENT_NAME, parentNameFromExcel, "Parent Name should be same as " + siblingParentDB.getFullName());
        }
        //-------------Student-------------
        Student student = new Student();
        //student.setUsername(admissionNo);
        student.setSession(DateUtility.getCurrentAcademicSession());
        student.setParentUsername(siblingParentDB.getUsername());
        student.setParentType(siblingParentDB.getParentType());
        student.setFullName(studentFullName);
        student.setAdmissionNo(admissionNo);
        student.setClassName(className);
        student.setFatherName(extraFields.overrideFatherName(siblingStudent.getFatherName()));
        student.setMotherName(extraFields.overrideMotherName(siblingStudent.getMotherName()));
        student.setGuardianName(extraFields.overrideGuardianName(siblingStudent.getGuardianName()));
        student.setPermanentAddress(siblingStudent.getPermanentAddress());
        student.setCurrentAddress(siblingStudent.getCurrentAddress());
        student.setDob(extraFields.dob());
        student.setAdmissionDate(extraFields.admissionDate());
        student.setRollNo(extraFields.rollNo());
        student.setGender(extraFields.gender());
        //ParentACtions
        Map<String, String> parentActionsMapStudent = new HashMap<>();
        parentActionsMapStudent.put(PARENT_ACTIONS, "true");
        student.setParentActions(parentActionsMapStudent);
        Map<String, String> studentDocuments = new HashMap<>();
        student.setDocuments(studentDocuments);

        List<Student> studentListForDB = new ArrayList<>();
        studentListForDB.add(student);
        studentParentUserDTO.setStudentList(studentListForDB);

        //Temporary Fields- please use else comment whatever needed as per school
        student.setAdmissionStatus(Student.AdmissionStatus.valueOf(admissionStatusFromExcel));
        student.setPermanentAddress(siblingStudent.getPermanentAddress());
        student.setCurrentAddress(siblingStudent.getCurrentAddress());

        //-------------Student Other Info-------------
        StudentOtherInfo studentOtherInfo = new StudentOtherInfo();
        //studentOtherInfo.setUsername(admissionNo);
        studentOtherInfo.setFamilyId(siblingStudentInfo.getFamilyId());
        //father
        studentOtherInfo.setFatherDob(siblingStudentInfo.getFatherDob());
        studentOtherInfo.setFatherAadharNo(siblingStudentInfo.getFatherAadharNo());
        studentOtherInfo.setFatherOccupation(siblingStudentInfo.getFatherOccupation());
        studentOtherInfo.setFatherEducation(siblingStudentInfo.getFatherEducation());
        //Mother
        studentOtherInfo.setMotherDob(siblingStudentInfo.getMotherDob());
        studentOtherInfo.setMotherAadharNo(siblingStudentInfo.getMotherAadharNo());
        studentOtherInfo.setMotherOccupation(siblingStudentInfo.getMotherOccupation());
        studentOtherInfo.setMotherEducation(siblingStudentInfo.getMotherEducation());
        //Guardian
        studentOtherInfo.setGuardianDob(siblingStudentInfo.getGuardianDob());
        studentOtherInfo.setGuardianAadharNo(siblingStudentInfo.getGuardianAadharNo());
        studentOtherInfo.setGuardianOccupation(siblingStudentInfo.getGuardianOccupation());
        studentOtherInfo.setGuardianEducation(siblingStudentInfo.getGuardianEducation());
        studentOtherInfo.setGuardianGender(siblingStudentInfo.getGuardianGender());

        List<StudentOtherInfo> studentOtherInfoListForDB = new ArrayList<>();
        studentOtherInfoListForDB.add(studentOtherInfo);
        studentParentUserDTO.setStudentOtherInfoList(studentOtherInfoListForDB);

    }


    private void validateParentPhoneNoInExcel(List<User> userListDB, String phoneNo, String excelFileName, int rowIndex) {
        Set<String> phoneNosInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getPhoneNo() != null && !u.getPhoneNo().isBlank())
                        .map(u -> u.getPhoneNo().trim())
                        .collect(Collectors.toSet());

        if (phoneNosInUserListDB.contains(phoneNo)) {
            throwExcelException(excelFileName, rowIndex, COL_PHONE, phoneNo, "already exists in Parent User Data");
        }
    }

    private void validateParentEmailInExcel(List<User> userListDB, String email, String excelFileName, int rowIndex) {
        Set<String> emailsInUserListDB =
                userListDB.stream()
                        .filter(u -> u != null && u.getEmail() != null && !u.getEmail().isBlank())
                        .map(u -> u.getEmail().trim())
                        .collect(Collectors.toSet());
        if (emailsInUserListDB.contains(email)) {
            throwExcelException(excelFileName, rowIndex, COL_PARENT_EMAIL, email, "already exists in Parent User Data");
        }
    }

    private void validateStudentAdmissionNoInExcel(List<Student> studentListDB, String admissionNo, String excelFileName, int rowIndex) {
        Set<String> admissionNoInStudentListDB =
                studentListDB.stream()
                        .filter(s -> s != null && s.getAdmissionNo() != null && !s.getAdmissionNo().isBlank())
                        .map(s -> s.getAdmissionNo().trim())
                        .collect(Collectors.toSet());
        if (admissionNoInStudentListDB.contains(admissionNo)) {
            throwExcelException(excelFileName, rowIndex, COL_ADMISSION_NO, admissionNo, "already exists in Student Data");
        }
    }

    private void validateClassNamePresentInClassCircularInExcel(List<ClassCircular> classCircularsDB, String className, String excelFileName, int rowIndex) {
        Set<String> classCirculars =
                classCircularsDB.stream()
                        .filter(t -> t != null && t.getClassName() != null && !t.getClassName().isBlank())
                        .map(t -> t.getClassName().trim())
                        .collect(Collectors.toSet());


        if (!classCirculars.contains(className)) {
            throwGenericExcelException(excelFileName, "Class Name", className, " Not present in class circular data");
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