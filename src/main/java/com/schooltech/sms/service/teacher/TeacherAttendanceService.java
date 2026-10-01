package com.schooltech.sms.service.teacher;


import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.teacher.dto.MarkTeacherAttendanceDTO;
import com.schooltech.sms.dao.client.attendence.TeacherAttendenceRepository;
import com.schooltech.sms.dao.client.teacher.TeacherRepository;
import com.schooltech.sms.entity.client.attendence.AttendanceAuditEntry;
import com.schooltech.sms.entity.client.attendence.TeacherAttendance;
import com.schooltech.sms.entity.client.teacher.Teacher;
import com.schooltech.sms.exception.TeacherNotFoundException;
import com.schooltech.sms.service.communication.EmailService;
import com.schooltech.sms.utility.AttendanceAuditUtil;
import com.schooltech.sms.utility.DateUtility;
import jakarta.persistence.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherAttendanceService {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthService authService;

    @Autowired
    private TeacherAttendenceRepository teacherAttendenceRepository;

    public ResponseEntity<List<MarkTeacherAttendanceDTO>> markAttendenceTeacher(List<TeacherAttendance> teacherAttendances) {  //sdn one object if attendence to be marked for single teacher
        List<MarkTeacherAttendanceDTO> teacherFullNameList = new ArrayList<>();
        teacherAttendances.stream().forEach(teacherAttendance -> {
            Optional<Teacher> teacher = teacherRepository.findByUsername(teacherAttendance.getUsername());
            teacher.orElseThrow(() -> new TeacherNotFoundException("You are marking attendance for invalid Teacher"));
            MarkTeacherAttendanceDTO markTeacherAttendanceDTO = new MarkTeacherAttendanceDTO();
            markTeacherAttendanceDTO.setFullName(teacher.get().getFullName());
            markTeacherAttendanceDTO.setRole(AppConstant.TEACHER_ROLE);
            teacherFullNameList.add(markTeacherAttendanceDTO);
            //check for Existing Attendance
            TeacherAttendance teacherAttendanceDB = teacherAttendenceRepository.findTeacherAttendenceByTeacherIdAndDate(teacherAttendance.getUsername(), teacherAttendance.getDate());

            List<AttendanceAuditEntry> existingAudit = null;
            if (teacherAttendanceDB != null) {
                teacherAttendance.setId(teacherAttendanceDB.getId());
                existingAudit = teacherAttendanceDB.getUpdatedBy();
                if (teacherAttendance.getMetaData() == null) {
                    teacherAttendance.setMetaData(teacherAttendanceDB.getMetaData());
                }
            }

            List<AttendanceAuditEntry> incoming = teacherAttendance.getUpdatedBy();
            String action = "update";
            String updatedBy = "unknown";
            if (incoming != null && !incoming.isEmpty()) {
                AttendanceAuditEntry entry = incoming.get(0);
                if (entry.getAction() != null) {
                    action = entry.getAction();
                } else if (teacherAttendance.getStatus() != null) {
                    action = teacherAttendance.getStatus().name();
                }
                if (entry.getUpdatedBy() != null) {
                    updatedBy = entry.getUpdatedBy();
                }
            }

            teacherAttendance.setUpdatedBy(AttendanceAuditUtil.append(existingAudit, action, updatedBy));
            teacherAttendance.setTimestamp(DateUtility.getCurrentTimeStamp());
        });
        teacherAttendenceRepository.saveAllAndFlush(teacherAttendances);
        return ResponseEntity.ok(teacherFullNameList);
    }

    public ResponseEntity<String> updateTeachersAttendance(List<TeacherAttendance> teacherAttendanceList) {
        teacherAttendanceList.forEach(teacherAttendance -> {
            teacherAttendenceRepository.findTeacherAttendenceByTeacherIdAndDate2(teacherAttendance.getUsername(), teacherAttendance.getDate()).ifPresentOrElse(existingTeacherAttendance -> {
                updateNonNullFields(existingTeacherAttendance, teacherAttendance);

                List<AttendanceAuditEntry> incoming = teacherAttendance.getUpdatedBy();
                String action = "update";
                String updatedBy = "unknown";
                if (incoming != null && !incoming.isEmpty()) {
                    AttendanceAuditEntry entry = incoming.get(0);
                    if (entry.getAction() != null) {
                        action = entry.getAction();
                    } else if (teacherAttendance.getApproved() != null) {
                        action = "approval:" + teacherAttendance.getApproved().name();
                    } else if (teacherAttendance.getStatus() != null) {
                        action = teacherAttendance.getStatus().name();
                    }
                    if (entry.getUpdatedBy() != null) {
                        updatedBy = entry.getUpdatedBy();
                    }
                }

                existingTeacherAttendance.setUpdatedBy(
                        AttendanceAuditUtil.append(existingTeacherAttendance.getUpdatedBy(), action, updatedBy));
                existingTeacherAttendance.setTimestamp(DateUtility.getCurrentTimeStamp());
                teacherAttendenceRepository.save(existingTeacherAttendance);
            }, () -> {
                throw new RuntimeException("Teacher Attendance: " + teacherAttendance.getUsername() + " not present. Aborting Update");
            });
        });
        return ResponseEntity.ok("Teacher Attendance Data Updated");
    }

    private void updateNonNullFields(TeacherAttendance existingTeacherAttendance, TeacherAttendance newTeacherAttendance) {
        if (newTeacherAttendance.getStatus() != null)
            existingTeacherAttendance.setStatus(newTeacherAttendance.getStatus());
        if (newTeacherAttendance.getCheckInTime() != null)
            existingTeacherAttendance.setCheckInTime(newTeacherAttendance.getCheckInTime());
        if (newTeacherAttendance.getCheckOutTime() != null)
            existingTeacherAttendance.setCheckOutTime(newTeacherAttendance.getCheckOutTime());
        if (newTeacherAttendance.getApproved() != null)
            existingTeacherAttendance.setApproved(newTeacherAttendance.getApproved());
    }

    public ResponseEntity<TeacherAttendance> getTeacherAttendence(String teacherId, LocalDate date) {
        try {
            TeacherAttendance teacherAttendance = teacherAttendenceRepository.findTeacherAttendenceByTeacherIdAndDate(teacherId, date);
            if (teacherAttendance == null) {
                throw new TeacherNotFoundException("Teacher attendance not found");
            }
            return new ResponseEntity<>(teacherAttendance, HttpStatus.OK);
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("Multiple attendance records found for teacher " + teacherId + " on " + date, e);
        } catch (TeacherNotFoundException e) {
            throw e; // Re-throw custom exception to allow custom handler to catch it
        } catch (Exception e) {
            // Catch any unexpected errors (e.g., DB connection issues)
            throw new RuntimeException("An unexpected error occurred while fetching teacher attendance", e);
        }
    }

    public ResponseEntity<List<TeacherAttendance>> getTeacherAttendenceHistory(String teacherId, LocalDate startDate, LocalDate endDate) {
        List<TeacherAttendance> teacherAttendances = teacherAttendenceRepository.findAllAttendenceBetweenDates(teacherId, startDate, endDate);
        return new ResponseEntity<>(teacherAttendances, HttpStatus.OK);
    }


    public ResponseEntity<List<TeacherAttendance>> getTeacherAttendencesByDate(LocalDate date) {
        List<TeacherAttendance> teacherAttendances = teacherAttendenceRepository.findByDate(date);
        return new ResponseEntity<>(teacherAttendances, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteTeacherAttendancesForDate(LocalDate date) {
        try {
            List<TeacherAttendance> teacherAttendances = teacherAttendenceRepository.findByDate(date);
            teacherAttendances.stream().forEach((teacherAttendance) -> {
                teacherAttendenceRepository.delete(teacherAttendance);
            });
            return new ResponseEntity<>("Teacher Attendances for Date: " + date + " has been deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable To Delete Teacher Attendances for Date: " + date, HttpStatus.NOT_FOUND);
        }
    }
}
