package com.schooltech.sms.utility;

import com.schooltech.sms.entity.client.attendence.AttendanceAuditEntry;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceAuditUtil {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private AttendanceAuditUtil() {
        // utility class — no instances
    }

    /**
     * Appends a new audit entry (current time, action, updatedBy) and keep existing
     *
     * @param existing the attendance record's current updatedBy list (may be null)
     * @param action   what happened, e.g. "present", "absent", "auto-marked absent"
     * @param updatedBy    who did it, e.g. "carol danves (teacher1)" or "cron job"
     * @return a new list with the appended entry
     */
    public static List<AttendanceAuditEntry> append(
            List<AttendanceAuditEntry> existing, String action, String updatedBy) {
        List<AttendanceAuditEntry> updated = existing != null ? new ArrayList<>(existing) : new ArrayList<>();
        String timeKey = LocalTime.now(IST).format(TIME_FORMAT);
        updated.add(new AttendanceAuditEntry(timeKey, action, updatedBy));
        return updated;
    }
}