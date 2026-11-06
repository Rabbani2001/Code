//package com.schooltech.sms.scheduler;
//
//import com.schooltech.multitenant.TenantContext;
//import com.schooltech.sms.constant.ClientConfig;
//import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
//import com.schooltech.sms.dao.client.payment.StudentFeeRepository;
//import com.schooltech.sms.dao.client.student.StudentRepository;
//import com.schooltech.sms.dao.master.TenantRepository;
//import com.schooltech.sms.entity.client.circulars.ClassCircular;
//import com.schooltech.sms.entity.client.payment.StudentFee;
//import com.schooltech.sms.entity.client.student.Student;
//import com.schooltech.sms.entity.master.SchoolTenant;
//import com.schooltech.sms.utility.FeeUtility;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Pattern;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class FeeReminderCronJob {
//
//    private final TenantRepository tenantRepository;
//    private final ClassCircularRepository classCircularRepository;
//    private final StudentRepository studentRepository;
//    private final StudentFeeRepository studentFeeRepository;
//    // TODO: inject your actual SMS/notification service here, e.g.:
//    // private final SmsService smsService;
//
//    private static final Pattern SCHEDULE_SUFFIX = Pattern.compile("_\\d+$");
//    private static final int REMINDER_LEAD_DAYS = 2;
//    private static final int DEFAULT_LATE_FEE_START_DAY = 1; // adjust to match your frontend's AppConstant.LATE_FEE_DEFAULT_START_DATE
//
//    // Runs once daily — adjust the time to whatever suits your ops window.
//    // Uses the same per-tenant, TenantContext-scoped pattern as EmployeeAttendanceCronJob.
//    @Scheduled(cron = "0 05 18 * * *", zone = "Asia/Kolkata")
//    public void sendLateFeeReminders() {
//        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
//        LocalDate targetDueDate = today.plusDays(REMINDER_LEAD_DAYS);
//
//        List<SchoolTenant> tenants = tenantRepository.findAll();
//
//        for (SchoolTenant tenant : tenants) {
//            if (!ClientConfig.isFeeReminderCronEnabledForTenant(tenant.getTenantCode())) {
//                continue;
//            }
//            try {
//                TenantContext.setCurrentTenant(tenant.getTenantId());
//
//                // calls the processTenant method
//                processTenant(tenant.getTenantCode(), targetDueDate);
//            } catch (Exception e) {
//                log.error("Fee reminder cron failed for tenant [{}] → {}", tenant.getTenantCode(), e.getMessage(), e);
//            } finally {
//                TenantContext.clear();
//            }
//        }
//    }
//
//    private void processTenant(String tenantCode, LocalDate targetDueDate) {
//        List<ClassCircular> allClassCirculars = classCircularRepository.findAll();
//
//        for (ClassCircular classCircular : allClassCirculars) {
//            Map<String, Double> schoolFeesRules = classCircular.getSchoolFeesRules();
//
//            // get lateFeeStartDay and if lateFeeStartDay is null return 1
//            int lateFeeStartDay = resolveLateFeeStartDay(schoolFeesRules);
//
//            Map<String, Double> monthlyFees = classCircular.getSchoolMonthlyFees();
//            if (monthlyFees == null || monthlyFees.isEmpty()) continue;
//
//            for (String monthName : monthlyFees.keySet()) {
//
//                // get dueDate from FeeUtility
//                LocalDate dueDate = FeeUtility.resolveDueDate(monthName, lateFeeStartDay, classCircular.getSession());
//                if (dueDate == null || !dueDate.equals(targetDueDate)) continue;
//
//                notifyPendingStudentsForMonth(classCircular, monthName, tenantCode);
//            }
//        }
//    }
//
//    private void notifyPendingStudentsForMonth(ClassCircular classCircular, String monthName, String tenantCode) {
//        Double monthFee = classCircular.getSchoolMonthlyFees().get(monthName);
//        if (monthFee == null || monthFee <= 0) return;
//
//        List<Student> studentsInClass = studentRepository.findByClassNameAndSessionOrderByFullNameAsc(
//                classCircular.getClassName(), classCircular.getSession());
//
//        for (Student student : studentsInClass) {
//            StudentFee studentFee = studentFeeRepository.findByStudentIdAndSession(
//                    student.getUsername(), classCircular.getSession());
//            if (studentFee == null) continue;
//
//            double paidForMonth = aggregatePaidForMonth(studentFee.getSchoolMonthlyFees(), monthName);
//            if (paidForMonth >= monthFee) continue;
//
//            double pendingForMonth = monthFee - paidForMonth;
//
//            sendFeeReminder(student, classCircular.getSession(), monthName, pendingForMonth, tenantCode);
//        }
//    }
//
//    /**
//     * Mirrors aggregatePaymentAmount from the frontend, but scoped to one month:
//     * sums all schedule keys like "april_1", "april_2" into one paid total for "april".
//     */
//    private double aggregatePaidForMonth(Map<String, Double> schoolMonthlyFees, String monthName) {
//        if (schoolMonthlyFees == null || schoolMonthlyFees.isEmpty()) return 0;
//
//        double total = 0;
//        for (Map.Entry<String, Double> entry : schoolMonthlyFees.entrySet()) {
//            Double value = entry.getValue();
//            if (value == null || value <= 0) continue;
//
//            String baseMonth = SCHEDULE_SUFFIX.matcher(entry.getKey()).replaceAll("");
//            if (baseMonth.equalsIgnoreCase(monthName)) {
//                total += value;
//            }
//        }
//        return total;
//    }
//
//    private void sendFeeReminder(Student student, String session, String monthName, double pendingAmount, String tenantCode) {
//        String message = String.format(
//                "Dear Parent, the %s fee (₹%.2f) for %s is due in %d days. "
//                        + "Please clear it before the due date to avoid late charges.",
//                capitalize(monthName), pendingAmount, student.getFullName(), REMINDER_LEAD_DAYS
//        );
//
//        // TODO: replace with your actual SMS/notification integration, e.g.:
//        // smsService.send(student.getParentPhoneNumber(), message);
//        log.info("[{}] Fee reminder queued for {} ({}): {}", tenantCode, student.getUsername(), student.getFullName(), message);
//    }
//
//    private String capitalize(String s) {
//        if (s == null || s.isEmpty()) return s;
//        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
//    }
//
//    private int resolveLateFeeStartDay(Map<String, Double> schoolFeesRules) {
//        if (schoolFeesRules == null) return DEFAULT_LATE_FEE_START_DAY;
//
//        Double lateFeeStartDate = schoolFeesRules.get("lateFeeStartDate");
//        if (lateFeeStartDate == null || lateFeeStartDate < 1) return DEFAULT_LATE_FEE_START_DAY;
//
//        return lateFeeStartDate.intValue();
//    }
//}