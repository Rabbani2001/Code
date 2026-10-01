package com.schooltech.sms.scheduler.attendance;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.constant.ClientConfig;
import com.schooltech.sms.dao.client.attendence.StaffAttendanceRepository;
import com.schooltech.sms.dao.client.attendence.StudentAttendenceRepository;
import com.schooltech.sms.dao.client.attendence.TeacherAttendenceRepository;
import com.schooltech.sms.dao.client.circulars.HolidayCircularRepository;
import com.schooltech.sms.dao.client.staff.StaffRepository;
import com.schooltech.sms.dao.client.student.StudentRepository;
import com.schooltech.sms.dao.client.teacher.TeacherRepository;
import com.schooltech.sms.dao.master.TenantRepository;
import com.schooltech.sms.entity.client.attendence.StaffAttendance;
import com.schooltech.sms.entity.client.attendence.StudentAttendance;
import com.schooltech.sms.entity.client.attendence.TeacherAttendance;
import com.schooltech.sms.entity.client.staff.Staff;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.teacher.Teacher;
import com.schooltech.sms.entity.master.SchoolTenant;
import com.schooltech.sms.utility.AttendanceAuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeAttendanceCronJob {

    private final TenantRepository tenantRepository;

    // ─── Teacher ──────────
    private final TeacherRepository teacherRepository;
    private final TeacherAttendenceRepository teacherAttendenceRepository;

    // ─── Staff ────────────
    private final StaffRepository staffRepository;
    private final StaffAttendanceRepository staffAttendanceRepository;

    // ─── Student ──────────
    private final StudentRepository studentRepository;
    private final StudentAttendenceRepository studentAttendenceRepository;

    private final HolidayCircularRepository holidayCircularRepository;

    // scheduler wakes up every minute MON-SAT
    // but actually performs work only once at ATTENDANCE_CRONJOB_TIME.
    // Cron format: second minute hour day month day-of-week
    @Scheduled(cron = "0 0 19 * * MON-SAT", zone = "Asia/Kolkata")
    public void autoMarkAbsentForTest() {
        autoMarkAbsentForTenant("test", "stclient_test");
    }

    @Scheduled(cron = "0 0 19 * * MON-SAT", zone = "Asia/Kolkata")
    public void autoMarkAbsentForDhs() {
        autoMarkAbsentForTenant("dhs", "stclient_dhs");
    }

    @Scheduled(cron = "0 0 19 * * MON-SAT", zone = "Asia/Kolkata")
    public void autoMarkAbsentForIps() {
        autoMarkAbsentForTenant("ips", "stclient_ips");
    }

    @Scheduled(cron = "0 0 19 * * MON-SAT", zone = "Asia/Kolkata")
    public void autoMarkAbsentForMahs() {
        autoMarkAbsentForTenant("mahs", "stclient_mahs");
    }

    @Scheduled(cron = "0 0 19 * * MON-SAT", zone = "Asia/Kolkata")
    public void autoMarkAbsentForMahsp() {
        autoMarkAbsentForTenant("mahsp", "stclient_mahsp");
    }

    @Scheduled(cron = "0 0 20 * * MON-SAT", zone = "Asia/Kolkata")
    public void autoMarkAbsentForJfpl() {
        autoMarkAbsentForTenant("jfpl", "stclient_jfpl");
    }

    @Scheduled(cron = "0 0 19 * * MON-SAT", zone = "Asia/Kolkata")
    public void autoMarkAbsentForEes() { autoMarkAbsentForTenant("ees", "stclient_ees"); }

    private void autoMarkAbsentForTenant(String tenantCode, String dbName) {

        // Check whether attendance cron is enabled for this tenant
        if (!ClientConfig.isAttendanceCronEnabled(tenantCode)) {
            log.info("[{}] Attendance cron is disabled", tenantCode);
            return;
        }

        Optional<SchoolTenant> schoolTenantOptional =
                tenantRepository.findByTenantCode(tenantCode);

        if (schoolTenantOptional.isEmpty()) {
            log.error("Tenant not found for code: {}", tenantCode);
            return;
        }

        SchoolTenant schoolTenant = schoolTenantOptional.get();

        // get current date and time
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        String currentTimeStr = LocalTime.now(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("HH:mm"));

        try {
            TenantContext.setCurrentTenant(schoolTenant.getTenantId()); //Need to check this will be 100% solution or not, create something like await

            // Holiday check — skip this tenant if today is a holiday
            if (holidayCircularRepository.existsByDate(today)) {
                return;
            }
            // Not a holiday → process absent for teachers and staff
            processAbsentForTeachers(schoolTenant.getTenantCode(), schoolTenant.getAttendanceControl(), today, currentTimeStr);
            processAbsentForStaff(schoolTenant.getTenantCode(), schoolTenant.getAttendanceControl(), today, currentTimeStr);
            processAbsentForStudents(today, currentTimeStr);

            log.info("Tenant: [{}] attendance cron completed successfully for today: {}", tenantCode, today);
        } catch (Exception e) {
            log.error("Error for Tenant:[{}] for today: {} → {}",
                    schoolTenant.getTenantCode(), today, e.getMessage(), e);
        } finally {
            TenantContext.clear();
        }
    }

    // below code is for attendanceControl

//    @Scheduled(cron = "0 * * * * MON-SAT")
//    public void autoMarkAbsentAfterCutoff() {
//
//        LocalDate today = LocalDate.now(IST);
//        String currentTimeStr = LocalTime.now(IST).format(DateTimeFormatter.ofPattern("HH:mm"));
//        log.info("⏰ Cron running at: {}", currentTimeStr);
//
//        List<SchoolTenant> activeTenants = tenantRepository.findAllActiveTenants();
//        //log.info("🏫 Active tenants: {}", activeTenants.size());
//
//        for (SchoolTenant tenant : activeTenants) {
//            try {
//                Map<String, String> attendanceControl = tenant.getAttendanceControl();
//                if (attendanceControl == null) {
//                    log.warn("⚠️ No attendanceControl configured for tenant: {}", tenant.getTenantCode());
//                    continue;
//                }
//
//                String checkoutCutoffStr = attendanceControl.get("teacherAttendanceCheckoutCutoffTime");
//                if (checkoutCutoffStr == null) {
//                    log.warn("⚠️ No teacherAttendanceCheckoutCutoffTime for tenant: {}", tenant.getTenantCode());
//                    continue;
//                }
//
//                String triggerTimeStr = LocalTime.parse(checkoutCutoffStr)
//                        .plusMinutes(1)
//                        .format(DateTimeFormatter.ofPattern("HH:mm"));
//
//                //log.info("🕐 Tenant [{}] cutoff: {}, trigger: {}, now: {}",
//                //      tenant.getTenantCode(), checkoutCutoffStr, triggerTimeStr, currentTimeStr);
//
//                if (!currentTimeStr.equals(triggerTimeStr)) continue;
//
//                log.info("✅ Trigger matched for tenant: {} (tenantId={})",
//                        tenant.getTenantCode(), tenant.getTenantId());
//
//                TenantContext.setCurrentTenant(tenant.getTenantId());
//                processAbsentForTenant(tenant.getTenantCode(), today, currentTimeStr);
//
//            } catch (Exception e) {
//                log.error("❌ Error for tenant: {} → {}", tenant.getTenantCode(), e.getMessage(), e);
//            } finally {
//                TenantContext.clear();
//            }
//        }
//    }


    // ─── Teacher absent processing ────────────────────────────────────────────

    private void processAbsentForTeachers(
            String tenantCode,
            Map<String, String> attendanceControl,
            LocalDate today,
            String currentTime) {

        String schoolStartTime = (attendanceControl != null && attendanceControl.get("teacherAttendanceMarkCutoffTime") != null)
                ? attendanceControl.get("teacherAttendanceMarkCutoffTime")
                : null;

        int extendedCheckinMinutes = ClientConfig.getAttendanceExtendedCheckinMinutes(tenantCode);

        // get all teachers for the tenant
        List<Teacher> allTeachers = teacherRepository.findAll();
        log.info(" Total teachers in tenant [{}]: {}", tenantCode, allTeachers.size());

        //int markedAbsent = 0, updatedToAbsent = 0, skipped = 0;

        for (Teacher teacher : allTeachers) {
            String username = teacher.getUsername();

            TeacherAttendance teacherAttendanceDB = teacherAttendenceRepository
                    .findByUsernameAndDate(username, today)
                    .orElse(null);

            if (teacherAttendanceDB == null) {
                TeacherAttendance absent = new TeacherAttendance();
                absent.setUsername(username);
                absent.setDate(today);
                absent.setStatus(TeacherAttendance.Status.absent);
                absent.setCheckInTime(LocalTime.parse(currentTime));//check format saved in db
                absent.setCheckOutTime(LocalTime.parse(currentTime));
                absent.setApproved(null);

                // Set metadata
                Map<String, String> metaData = new HashMap<>();
                metaData.put("schoolStartTime", schoolStartTime);
                metaData.put("attendanceExtendedCheckinMinutes", String.valueOf(extendedCheckinMinutes));
                absent.setMetaData(metaData);

                absent.setUpdatedBy(AttendanceAuditUtil.append(null, "absent", "cron"));//null because no data is present to handle
                absent.setTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))));
                teacherAttendenceRepository.save(absent);
                //markedAbsent++;
                log.info("Teacher New ABSENT → {}", username);

            } else if (TeacherAttendance.Status.present.equals(teacherAttendanceDB.getStatus())
                    && teacherAttendanceDB.getCheckOutTime() == null) {
                teacherAttendanceDB.setStatus(TeacherAttendance.Status.absent);
                teacherAttendanceDB.setCheckOutTime(LocalTime.parse(currentTime));
                teacherAttendanceDB.setUpdatedBy(
                        AttendanceAuditUtil.append(teacherAttendanceDB.getUpdatedBy(), "absent as no-checkout", "cron"));
                teacherAttendanceDB.setTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))));
                teacherAttendenceRepository.save(teacherAttendanceDB);
                //updatedToAbsent++;
                log.info("Teacher Updated to ABSENT (no checkout) → {}", username);

            }
            //else {
            //skipped++;
            //}
        }

//        log.info("Teachers [{}] → New Absent: {}, Updated: {}, Skipped: {}",
//                tenantCode, markedAbsent, updatedToAbsent, skipped);
    }

    // ─── Staff absent processing ──────────────────────────────────────────────

    private void processAbsentForStaff(
            String tenantCode,
            Map<String, String> attendanceControl,
            LocalDate today,
            String currentTime) {

        // get school start time from client config
        String schoolStartTime = (attendanceControl != null && attendanceControl.get("teacherAttendanceMarkCutoffTime") != null)
                ? attendanceControl.get("teacherAttendanceMarkCutoffTime")
                : null;

        // get extendedCheckinMinutes from client config
        int extendedCheckinMinutes = ClientConfig.getAttendanceExtendedCheckinMinutes(tenantCode);

        // findAll() already filters is_active=true via @Where on Staff entity
        // Exclude superadmin
        List<Staff> allStaff = staffRepository.findAll()
                .stream()
                .filter(s -> !AppConstant.SUPER_ADMIN_ROLE.equalsIgnoreCase(s.getRole()))
                .toList();

        //log.info("Total staff in tenant [{}]: {}", tenantCode, allStaff.size());

        //int markedAbsent = 0, updatedToAbsent = 0, skipped = 0;

        for (Staff staff : allStaff) {
            String username = staff.getUsername();

            StaffAttendance staffAttendanceDB = staffAttendanceRepository
                    .findByUsernameAndDate(username, today)
                    .orElse(null);

            if (staffAttendanceDB == null) {
                StaffAttendance absent = new StaffAttendance();
                absent.setUsername(username);
                absent.setDate(today);
                absent.setStatus(StaffAttendance.Status.absent);
                absent.setCheckInTime(LocalTime.parse(currentTime));
                absent.setCheckOutTime(LocalTime.parse(currentTime));
                absent.setApproved(null);

                // Set metadata
                Map<String, String> metaData = new HashMap<>();
                metaData.put("schoolStartTime", schoolStartTime);
                metaData.put("attendanceExtendedCheckinMinutes", String.valueOf(extendedCheckinMinutes));
                absent.setMetaData(metaData);

                absent.setUpdatedBy(AttendanceAuditUtil.append(null, "absent", "cron"));
                absent.setTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))));
                staffAttendanceRepository.save(absent);
                //markedAbsent++;
                //log.info("Staff New ABSENT → {}", username);

            } else if (StaffAttendance.Status.present.equals(staffAttendanceDB.getStatus())
                    && staffAttendanceDB.getCheckOutTime() == null) {
                staffAttendanceDB.setStatus(StaffAttendance.Status.absent);
                staffAttendanceDB.setCheckOutTime(LocalTime.parse(currentTime));
                staffAttendanceDB.setUpdatedBy(
                        AttendanceAuditUtil.append(staffAttendanceDB.getUpdatedBy(), "absent as no-checkout", "cron"));
                staffAttendanceDB.setTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))));
                staffAttendanceRepository.save(staffAttendanceDB);
                //updatedToAbsent++;
                //log.info("Staff Updated to ABSENT (no checkout) → {}", username);

            }
            //else {
            //skipped++;
            //log.info("⏭ Skipped → {}", username);
            //}
        }

//        log.info("Staff [{}] → New Absent: {}, Updated: {}, Skipped: {}",
//                tenantCode, markedAbsent, updatedToAbsent, skipped);
    }

    private void processAbsentForStudents(
            LocalDate today,
            String currentTime) {

        List<Student> allStudents = studentRepository.findAll();

        for (Student student : allStudents) {
            String username = student.getUsername();

            StudentAttendance studentAttendanceDB = studentAttendenceRepository
                    .findByUsernameAndDate(username, today)
                    .orElse(null);

            if (studentAttendanceDB == null) {
                StudentAttendance absent = new StudentAttendance();
                absent.setUsername(username);
                absent.setClassName(student.getClassName());
                absent.setDate(today);
                absent.setStatus(StudentAttendance.Status.absent);
                absent.setCheckInTime(LocalTime.parse(currentTime));
                absent.setCheckOutTime(LocalTime.parse(currentTime));
                absent.setApproved(null);
                absent.setTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata"))));
                studentAttendenceRepository.save(absent);
            }
        }
    }
}