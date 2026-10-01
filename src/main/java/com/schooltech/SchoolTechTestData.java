package com.schooltech;

import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.student.dto.StudentParentUserDTO;
import com.schooltech.sms.controller.teacher.dto.TeacherUserDTO;
import com.schooltech.sms.entity.client.circulars.BusCircular;
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.circulars.ExamSchedule;
import com.schooltech.sms.entity.client.circulars.HolidayCircular;
import com.schooltech.sms.entity.client.student.Parent;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.student.StudentOtherInfo;
import com.schooltech.sms.entity.client.teacher.Teacher;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.service.circular.BusCircularService;
import com.schooltech.sms.service.circular.ClassCircularService;
import com.schooltech.sms.service.circular.HolidayCircularService;
import com.schooltech.sms.service.student.StudentService;
import com.schooltech.sms.service.teacher.TeacherService;
import com.schooltech.sms.utility.DateUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.schooltech.sms.constant.AppConstant.*;

@Component
public class SchoolTechTestData {
    private static final Logger log = LoggerFactory.getLogger(SchoolTechTestData.class);
    @Autowired
    private ClassCircularService classCircularService;
    @Autowired
    private HolidayCircularService holidayCircularService;
    @Autowired
    private BusCircularService busCircularService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    public void prepareTestData() {

        if (!AppConstant.CREATE_TEST_DATA) return;

        try {
            prepareClassCircularData();
        } catch (Exception e) {
            log.error("==========================Error in prepareClassCircularData");
        }

        try {
            prepareHolidayCircularData();
        } catch (Exception e) {
            log.error("===========================Error in prepareHolidayCircularData");
        }

        try {
            prepareBusCircularData();
        } catch (Exception e) {
            log.error("==========================Error in prepareBusCircularData");
        }

        try {
            prepareStudentData();
        } catch (Exception e) {
            log.error("==========================Error in prepareStudentData");
        }

        try {
            prepareTeacherData();
        } catch (Exception e) {
            log.error("==========================Error in prepareTeacherData");
        }


    }

    public void prepareClassCircularData() {

        List<ClassCircular> classCircularList = new ArrayList<>();
        ClassCircular classCircular = new ClassCircular();

        classCircular.setClassName("bv1");
        classCircular.setSession("2025-2026");
        List<String> subjects = new ArrayList<>();
        subjects.add("english");
        subjects.add("maths");
        classCircular.setSubjects(subjects);

        Map<String, Double> schoolMonthlyFeesMap = new LinkedHashMap<>();
        int countList = -1;
        Double monthlyFee = 1000.0;
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        classCircular.setSchoolMonthlyFees(schoolMonthlyFeesMap);

        Map<String, Double> schoolMiscFeesMap = new LinkedHashMap<>();
        Double registrationFee = 100.0;
        Double admissionFee = 1000.0;
        Double annualFee = 1000.0;
        schoolMiscFeesMap.put(MISC_FEE_LIST.get(0), registrationFee);
        schoolMiscFeesMap.put(MISC_FEE_LIST.get(1), admissionFee);
        schoolMiscFeesMap.put(MISC_FEE_LIST.get(2), annualFee);
        classCircular.setSchoolMiscFees(schoolMiscFeesMap);

        Map<String, Double> schoolFeesRulesMap = new LinkedHashMap<>();
        Double lateFeeDayCharges = 10.0;
        Double lateFeeStartDate = 5.0;
        schoolFeesRulesMap.put(SCHOOL_FEE_RULES_LIST.get(0), lateFeeDayCharges);
        schoolFeesRulesMap.put(SCHOOL_FEE_RULES_LIST.get(1), lateFeeStartDate);
        classCircular.setSchoolFeesRules(schoolFeesRulesMap);

        Map<String, List<ExamSchedule>> exaMap = new HashMap<>();
        classCircular.setExamSchedules(exaMap);

        Map<String, List<String>> exaMap2 = new HashMap<>();
        classCircular.setClassQualities(exaMap2);

        classCircular.setGradeCircular(new ArrayList<>());

        classCircular.setTimestamp(DateUtility.getCurrentTimeStamp());

        classCircularList.add(classCircular);
        classCircularService.saveClassCirculars(classCircularList);
    }

    public void prepareHolidayCircularData() {
        List<HolidayCircular> holidayCircularList = new ArrayList<>();
        HolidayCircular holidayCircular = new HolidayCircular();

        holidayCircular.setHolidayName("new year");
        holidayCircular.setDate(DateUtility.convertStringToLocalDate("01-01-2026"));
        holidayCircular.setTimestamp(DateUtility.getCurrentTimeStamp());

        holidayCircularList.add(holidayCircular);
        holidayCircularService.saveHolidayCirculars(holidayCircularList);
    }

    public void prepareBusCircularData() {
        List<BusCircular> busCircularList = new ArrayList<>();
        BusCircular busCircular = new BusCircular();

        busCircular.setBusRoute("faridabad");
        busCircular.setSession("2025-2026");

        Map<String, Double> busMonthlyFeesMap = new LinkedHashMap<>();
        int countList = -1;
        Double monthlyFee = 1000.0;
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
        busCircular.setBusMonthlyFees(busMonthlyFeesMap);

        Map<String, Double> busFeesRulesMap = new LinkedHashMap<>();
        Double lateFeeDayCharges = 10.0;
        Double lateFeeStartDate = 5.0;
        busFeesRulesMap.put(SCHOOL_FEE_RULES_LIST.get(0), lateFeeDayCharges);
        busFeesRulesMap.put(SCHOOL_FEE_RULES_LIST.get(1), lateFeeStartDate);
        busCircular.setBusFeesRules(busFeesRulesMap);

        busCircular.setTimestamp(DateUtility.getCurrentTimeStamp());

        busCircularList.add(busCircular);
        busCircularService.saveBusCirculars(busCircularList);
    }

    public void prepareStudentData() {

        //User
        User user = new User();
        user.setEmail("tom@gmail.com");
        user.setPhoneNo("9988776601");
        user.setTenantId("stclient_default");
        user.setRole(AppConstant.PARENT_ROLE);
        user.setIsActive(true);
        user.setPassword("good@123");
        user.setTimestamp(DateUtility.getCurrentTimeStamp());
        //Parent
        Parent parent = new Parent();
        parent.setParentType(AppConstant.PARENT_ROLE);
        parent.setFullName("tom cat");
        parent.setTimestamp(DateUtility.getCurrentTimeStamp());
        //Student
        String admissionNo = "test01";
        Student student = new Student();
        student.setSession(DateUtility.getCurrentAcademicSession());
        student.setParentType("father");
        student.setFullName("jerry mouse");
        student.setAdmissionNo(admissionNo);
        student.setAdmissionStatus(Student.AdmissionStatus.NEW);
        student.setAdmissionDate(DateUtility.convertStringToLocalDate("01-01-2025"));
        student.setClassName("bv1");
        student.setFatherName("tom father");
        student.setRollNo(1l);
        student.setTimestamp(DateUtility.getCurrentTimeStamp());
        //ParentActions
        Map<String, String> parentActionsMapStudent = new HashMap<>();
        parentActionsMapStudent.put(PARENT_ACTIONS, "true");
        student.setParentActions(parentActionsMapStudent);
        //Student Documents
        Map<String, String> studentDocuments = new HashMap<>();
        student.setDocuments(studentDocuments);

        List<Student> studentList = new ArrayList<>();
        studentList.add(student);

        //Student Other infp
        StudentOtherInfo studentOtherInfo = new StudentOtherInfo();
        studentOtherInfo.setUsername(admissionNo);
        studentOtherInfo.setTimestamp(DateUtility.getCurrentTimeStamp());
        List<StudentOtherInfo> studentOtherInfoList = new ArrayList<>();
        studentOtherInfoList.add(studentOtherInfo);

        StudentParentUserDTO studentParentUserDTO = new StudentParentUserDTO();
        studentParentUserDTO.setUser(user);
        studentParentUserDTO.setParent(parent);
        studentParentUserDTO.setStudentList(studentList);
        studentParentUserDTO.setStudentOtherInfoList(studentOtherInfoList);

        List<StudentParentUserDTO> studentParentUserDTOList = new ArrayList<>();
        studentParentUserDTOList.add(studentParentUserDTO);
        studentService.saveStudentWithAssociatedEntity(studentParentUserDTOList);
    }


    public void prepareTeacherData() {

        User user = new User();
        user.setEmail("afsana@gmail.com");
        user.setPhoneNo("9988776602");
        user.setAltPhoneNo("9988776602");
        user.setTenantId("stclient_default");
        user.setRole(AppConstant.TEACHER_ROLE);
        user.setIsActive(true);
        user.setPassword(AppConstant.DEFAULT_PASSWORD);
        user.setTimestamp(DateUtility.getCurrentTimeStamp());


        Teacher teacher = new Teacher();
        teacher.setFullName("afsana khan");
        teacher.setEmployeeId("101");
        teacher.setGender("female");
        teacher.setAadharNo("999999999991");
        List<String> classNames = new ArrayList<>();
        classNames.add("bv1");
        teacher.setClassNames(classNames);
        teacher.setFatherName("afsana father");
        teacher.setMotherName("afsana mother");
        teacher.setGuardianName("NA");
        teacher.setQualification("bed");
        teacher.setDesignation("tgt");
        teacher.setDob(DateUtility.convertStringToLocalDate("01-01-2000"));
        teacher.setPermanentAddress("na");
        teacher.setCurrentAddress("na");
        teacher.setJoiningDate(DateUtility.convertStringToLocalDate("01-01-2000"));
        teacher.setBusRoute("faridabad");

        List<TeacherUserDTO> teacherUserDTOList = new ArrayList<>();
        TeacherUserDTO teacherUserDTO = new TeacherUserDTO();
        teacherUserDTO.setUser(user);
        teacherUserDTO.setTeacher(teacher);
        teacherUserDTOList.add(teacherUserDTO);
        teacherService.saveTeachersWithEntity(teacherUserDTOList);
    }

}


