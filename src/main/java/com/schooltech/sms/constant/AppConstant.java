package com.schooltech.sms.constant;

import java.util.List;

public class AppConstant {
    //Production sensitive property
    public static final boolean CREATE_TEST_DATA = false;
    public static final String DEFAULT_PASSWORD = "good@123";
    public static final String TEACHER_ROLE = "teacher";//must be always capital in Letter
    //public static final String ACOUNTANT_ROLE = "accountant";//must be always capital in Letter
    public static final String ADMIN_ROLE = "admin";//must be always capital in Letter
    public static final String SUPER_ADMIN_ROLE = "superadmin";//must be always capital in Letter
    public static final String PARENT_ROLE = "parent";//must be always capital in Letter
    public static final String STUDENT_ROLE = "student";//must be always capital in Letter
    //public static final String SUPERVISOR_ROLE = "supervisor";//must be always capital in Letter
    public static final String FEE_TYPE_SEQUENCE = "fee";
    public static final String BUSFEE_TYPE_SEQUENCE = "busfee";
    public static final String RESULT_TYPE_SEQUENCE = "result";
    public static final String VISTOR_TYPE_SEQUENCE = "visitor";
    public static final String PROFILE_PHOTO = "profilePhoto";
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String ATTENDANCE_CRONJOB_TIME = "19:00";
    public static List<String> STAFF_ELIGIBLE_ROLES = List.of("accountant", "supervisor", "driver", "admin", "superadmin", "coordinator", "employee");//must be always capital in Letter
    public static List<String> MONTH_NAMES_LIST_FEE = List.of("april", "may", "june", "july", "august", "september", "october", "november", "december", "january", "february", "march");
    public static List<String> SCHOOL_FEE_RULES_LIST = List.of("lateFeeDayCharges", "lateFeeStartDate");
    public static List<String> MISC_FEE_LIST = List.of("registrationFee", "admissionFee", "annualFee");
    public static List<String> MISC_FEE_LIST_NEW = List.of("registrationFee", "admissionFee");
    public static List<String> DESIGNATION_DROPDOWN_OPTIONS = List.of("tgt", "pgt", "ntt", "prt", "pti", "exam_controller", "special_educator", "counsellor", "other");//must be always capital in Letter
    public static List<String> PARENT_TYPE_LIST = List.of("father", "mother", "guardian");//must be always capital in Letter
    public static String PARENT_ACTIONS = "isParentEditStudent";
    public static List<String> FEE_TYPE_LIST = List.of("single", "combine");
    //If any new fee type added in fee dropdown in fee circular it will also to be added here
    public static List<String> OTHER_FEE_SUPPORT_IN_FEES = List.of("computerfee", "examfee");
}
