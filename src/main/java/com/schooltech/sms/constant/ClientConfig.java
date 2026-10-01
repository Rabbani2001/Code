package com.schooltech.sms.constant;

import java.util.Map;

public class ClientConfig {

    private static final Map<String, Boolean> ATTENDANCE_CRON_TENANT = Map.of(
            "test", false,
            "dhs", true,
            "ips", true,
            "mahs", false,
            "mahsp", false,
            "kps", false,
            "jfpl", true,
            "ees", false
    );
    // ─── Test cron: only mahs and mahsp ──────────────────────────────
    private static final Map<String, Boolean> STUDENT_ATTENDANCE_SMS_CRON_TENANT = Map.of(
            "test", false,
            "dhs", false,
            "ips", false,
            "mahs", true,
            "mahsp", true,
            "kps", false,
            "jfpl", false,
            "ees", false
    );

    private static final Map<String, Integer> ATTENDANCE_EXTENDED_CHECKIN_MINUTES_TENANT = Map.of(
            "test", 15,
            "dhs", 15,
            "ips", 15,
            "mahs", 15,
            "mahsp", 15,
            "kps", 15,
            "jfpl", 15,
            "ees", 0
    );

    //  ───────────────────────── Below all methods are utility methods not the client configs ──────────────────────────────
    public static boolean isAttendanceCronEnabled(String tenantCode) {
        if (tenantCode == null) {
            return false;
        }
        return ATTENDANCE_CRON_TENANT.getOrDefault(
                tenantCode.toLowerCase(),
                false
        );
    }

    public static boolean isStudentAttendanceSMSCronEnabledForTenant(String tenantCode) {
        if (tenantCode == null) {
            return false;
        }
        return STUDENT_ATTENDANCE_SMS_CRON_TENANT.getOrDefault(
                tenantCode.toLowerCase(),
                false
        );
    }

    public static int getAttendanceExtendedCheckinMinutes(String tenantCode) {
        if (tenantCode == null) return 0;
        return ATTENDANCE_EXTENDED_CHECKIN_MINUTES_TENANT.getOrDefault(tenantCode.toLowerCase(), 0);
    }
}