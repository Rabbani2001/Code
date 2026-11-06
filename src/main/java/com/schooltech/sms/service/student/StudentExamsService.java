package com.schooltech.sms.service.student;

import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.dao.client.student.StudentExamsRepository;
import com.schooltech.sms.entity.client.student.StudentExamSchedule;
import com.schooltech.sms.entity.client.student.StudentExamScheduleGrade;
import com.schooltech.sms.entity.client.student.StudentExams;
import com.schooltech.sms.utility.DateUtility;
import com.schooltech.sms.utility.SessionSequenceUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class StudentExamsService {

    private final StudentExamsRepository studentExamsRepository;
    @Autowired
    private SessionSequenceUtility sessionSequenceUtility;

    public StudentExamsService(StudentExamsRepository studentExamsRepository) {
        this.studentExamsRepository = studentExamsRepository;
    }

    public List<StudentExams> getAll() {
        return studentExamsRepository.findAll();
    }

    public StudentExams getByUsernameAndSession(String username, String session) {
        return studentExamsRepository.findByUsernameAndSession(username, session).orElseThrow(() -> new RuntimeException("StudentExam not found for username: " + username + " and session: " + session));
    }

    public ResponseEntity<String> saveStudentExams(List<StudentExams> studentExamsList) {
        List<StudentExams> studentExamsListToSave = new ArrayList<>();
        studentExamsList.forEach(studentExams -> {
            studentExamsRepository.findByUsernameAndSession(studentExams.getUsername(), studentExams.getSession())
                    .ifPresentOrElse(existingStudent -> {
                        updateNonNullFields(existingStudent, studentExams);
                        existingStudent.setTimestamp(DateUtility.getCurrentTimeStamp());
                        studentExamsListToSave.add(existingStudent);
                    }, () -> {
                        String resultId = sessionSequenceUtility.generateSessionSequence(AppConstant.RESULT_TYPE_SEQUENCE);
                        studentExams.setResultId(resultId);
                        studentExams.setTimestamp(DateUtility.getCurrentTimeStamp());
                        studentExamsListToSave.add(studentExams);
                    });
        });
        studentExamsRepository.saveAll(studentExamsListToSave);
        return ResponseEntity.ok("Student Exam Data Saved");
    }

//    public ResponseEntity<String> updateStudentExams(List<StudentExams> studentExamsList) {
//        studentExamsList.forEach(studentExams -> {
//            studentExamsRepository.findByUsername(studentExams.getUsername()).ifPresentOrElse(existingStudentExams -> {
//                updateNonNullFields(existingStudentExams, studentExams);
//                existingStudentExams.setTimestamp(DateUtility.getCurrentTimeStamp());
//                studentExamsRepository.save(existingStudentExams);
//            }, () -> {
//                throw new RuntimeException("Student: " + studentExams.getUsername() + " not present. Aborting Update");
//            });
//        });
//
//        return ResponseEntity.ok("Student Exam Data Updated");
//    }

    private void updateNonNullFields(StudentExams existingStudentExams, StudentExams studentExams) {
//        if (studentExams.getSession() != null)
//            existingStudentExams.setSession(studentExams.getSession());
//        if (studentExams.getClassName() != null)
//            existingStudentExams.setClassName(studentExams.getClassName());
        if (studentExams.getRollNo() != null)
            existingStudentExams.setRollNo(studentExams.getRollNo());
        if (studentExams.getExamSchedules() != null)
            existingStudentExams.setExamSchedules(studentExams.getExamSchedules());
        if (studentExams.getExamSchedulesGrade() != null)
            existingStudentExams.setExamSchedulesGrade(studentExams.getExamSchedulesGrade());
        if (studentExams.getTestSchedules() != null)
            existingStudentExams.setTestSchedules(studentExams.getTestSchedules());
        if (studentExams.getTestSchedulesGrade() != null)
            existingStudentExams.setTestSchedulesGrade(studentExams.getTestSchedulesGrade());
        if (studentExams.getExamScheduleComments() != null)
            existingStudentExams.setExamScheduleComments(studentExams.getExamScheduleComments());
        if (studentExams.getTestScheduleComments() != null)
            existingStudentExams.setTestScheduleComments(studentExams.getTestScheduleComments());
        if (studentExams.getExamRemarks() != null)
            existingStudentExams.setExamRemarks(studentExams.getExamRemarks());
        if (studentExams.getClassQualities() != null)
            existingStudentExams.setClassQualities(studentExams.getClassQualities());
    }


    public void deleteByUsername(String username) {
        if (!studentExamsRepository.existsByUsername(username)) {
            throw new RuntimeException("Cannot delete. StudentExam not found with username: " + username);
        }
        studentExamsRepository.deleteByUsername(username);
    }

    // ---- Generic helper ----
    private <ScheduleType> boolean hasValueInExam(
            String className,
            String session,
            String examName,
            Function<StudentExams, Map<String, List<ScheduleType>>> scheduleMapGetter,
            Function<ScheduleType, ?> valueGetter) { // give ScheduleType, I'll give you back some value

        List<StudentExams> studentExams =
                studentExamsRepository.findAllByClassNameAndSession(className, session);

        for (StudentExams studentExam : studentExams) {
            Map<String, List<ScheduleType>> scheduleMap = scheduleMapGetter.apply(studentExam);
            if (scheduleMap == null) continue;

            List<ScheduleType> schedules = scheduleMap.get(examName);
            if (schedules == null) continue;

            for (ScheduleType schedule : schedules) {
                // valueGetter.apply(schedule) runs the getter that was passed in,ForExample For hasMarksInExam, that's schedule.getMarksObtained().
                if (schedule != null && valueGetter.apply(schedule) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    // ---- The four public methods ----
    public ResponseEntity<Boolean> hasMarksInExam(String className, String session, String examName) {
        return ResponseEntity.ok(hasValueInExam(className, session, examName,
                StudentExams::getExamSchedules,          // scheduleMapGetter
                StudentExamSchedule::getMarksObtained)); // valueGetter
    }

    public ResponseEntity<Boolean> hasMarksInTestExam(String className, String session, String examName) {
        return ResponseEntity.ok(hasValueInExam(className, session, examName,
                StudentExams::getTestSchedules,
                StudentExamSchedule::getMarksObtained));
    }

    public ResponseEntity<Boolean> hasGradesInExam(String className, String session, String examName) {
        return ResponseEntity.ok(hasValueInExam(className, session, examName,
                StudentExams::getExamSchedulesGrade,
                StudentExamScheduleGrade::getGrade));
    }

    public ResponseEntity<Boolean> hasGradesInTestExam(String className, String session, String examName) {
        return ResponseEntity.ok(hasValueInExam(className, session, examName,
                StudentExams::getTestSchedulesGrade,
                StudentExamScheduleGrade::getGrade));
    }
}
