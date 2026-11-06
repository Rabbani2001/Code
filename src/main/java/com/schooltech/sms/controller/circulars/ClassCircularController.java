package com.schooltech.sms.controller.circulars;

import com.schooltech.sms.controller.circulars.dto.*;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.dao.client.student.StudentExamsRepository;
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.circulars.ExamSchedule;
import com.schooltech.sms.entity.client.circulars.ExamScheduleGrade;
import com.schooltech.sms.entity.client.student.StudentExamSchedule;
import com.schooltech.sms.entity.client.student.StudentExamScheduleGrade;
import com.schooltech.sms.entity.client.student.StudentExams;
import com.schooltech.sms.service.circular.ClassCircularService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ClassCircularController {
    @Autowired
    private ClassCircularService classCircularService;
    @Autowired
    private ClassCircularRepository classCircularRepository;
    @Autowired
    private StudentExamsRepository studentExamsRepository;

    @GetMapping("/getClassCirculars")
    public ResponseEntity<List<ClassCircular>> getClassCirculars() {
        return classCircularService.getClassCirculars();
    }

    @GetMapping("/getClassCircularsBySession")
    public ResponseEntity<List<ClassCircular>> getClassCircularsBySession(@RequestParam String session) {
        return classCircularService.getClassCircularsBySession(session);
    }

    @GetMapping("/getClassCircularsByClassNames/{classNames}")
    public ResponseEntity<List<ClassCircular>> getClassCircularsByClassNames(@PathVariable String classNames, @RequestParam String session) {
        return classCircularService.getClassCircularsByClassNames(classNames, session);
    }

    @GetMapping("/isClassLinked/{classNames}")
    public ResponseEntity<Boolean> isClassLinked(@PathVariable String classNames, @RequestParam String session) {
        return classCircularService.isClassLinked(classNames, session);
    }

    @PostMapping("/saveClassCirculars")
    @Transactional
    public ResponseEntity<String> saveClassCircular(@RequestBody List<ClassCircular> classCirculars) {
        return classCircularService.saveClassCirculars(classCirculars);
    }

    @PutMapping("/updateClassCirculars")
    @Transactional
    public ResponseEntity<String> updateClassCirculars(@RequestBody List<ClassCircular> classCirculars) {
        ResponseEntity<String> response = classCircularService.updateClassCirculars(classCirculars);
        classCircularService.updateAssociatedFees(classCirculars);
        return response;
    }

    @DeleteMapping("/deleteClassCirculars/{className}")
    public ResponseEntity<String> deleteCLassCirculars(@PathVariable String className, @RequestParam String session) {
        return classCircularService.deleteClassCirculars(className, session);
    }

    //EXAM SCHEDULE
    @PutMapping("/updateExamName/{className}")
    @Transactional
    public ResponseEntity<String> updateExamName(@PathVariable String className, @RequestBody UpdateExamNameDTO updateExamNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getExamSchedules().containsKey(updateExamNameDTO.getOldExamName())) {
                classCircular.getExamSchedules().put(updateExamNameDTO.getNewExamName(), classCircular.getExamSchedules().remove(updateExamNameDTO.getOldExamName()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getExamSchedules().containsKey(updateExamNameDTO.getOldExamName())) {
                studentExam.getExamSchedules().put(updateExamNameDTO.getNewExamName(), studentExam.getExamSchedules().remove(updateExamNameDTO.getOldExamName()));
            }
            if (studentExam.getExamScheduleComments().containsKey(updateExamNameDTO.getOldExamName())) {
                studentExam.getExamScheduleComments().put(updateExamNameDTO.getNewExamName(), studentExam.getExamScheduleComments().remove(updateExamNameDTO.getOldExamName()));
            }
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Exam name updated from " + updateExamNameDTO.getOldExamName() + " to " + updateExamNameDTO.getNewExamName() + " in both tables.");
    }

    @PutMapping("/updateExamScheduleGradeName/{className}")
    @Transactional
    public ResponseEntity<String> updateExamScheduleGradeName(@PathVariable String className, @RequestBody UpdateExamNameDTO updateExamNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getExamSchedulesGrade().containsKey(updateExamNameDTO.getOldExamName())) {
                classCircular.getExamSchedulesGrade().put(updateExamNameDTO.getNewExamName(), classCircular.getExamSchedulesGrade().remove(updateExamNameDTO.getOldExamName()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getExamSchedulesGrade().containsKey(updateExamNameDTO.getOldExamName())) {
                studentExam.getExamSchedulesGrade().put(updateExamNameDTO.getNewExamName(), studentExam.getExamSchedulesGrade().remove(updateExamNameDTO.getOldExamName()));
            }
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Exam Schedule Grade name  updated from " + updateExamNameDTO.getOldExamName() + " to " + updateExamNameDTO.getNewExamName() + " in both tables.");
    }

    @PutMapping("/updateTestScheduleName/{className}")
    @Transactional
    public ResponseEntity<String> updateTestScheduleName(@PathVariable String className, @RequestBody UpdateExamNameDTO updateExamNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getTestSchedules().containsKey(updateExamNameDTO.getOldExamName())) {
                classCircular.getTestSchedules().put(updateExamNameDTO.getNewExamName(), classCircular.getTestSchedules().remove(updateExamNameDTO.getOldExamName()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getTestSchedules().containsKey(updateExamNameDTO.getOldExamName())) {
                studentExam.getTestSchedules().put(updateExamNameDTO.getNewExamName(), studentExam.getTestSchedules().remove(updateExamNameDTO.getOldExamName()));
            }
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Test Schedule name updated from " + updateExamNameDTO.getOldExamName() + " to " + updateExamNameDTO.getNewExamName() + " in both tables.");
    }

    @PutMapping("/updateTestScheduleGradeName/{className}")
    @Transactional
    public ResponseEntity<String> updateTestScheduleGradeName(@PathVariable String className, @RequestBody UpdateExamNameDTO updateExamNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getTestSchedulesGrade().containsKey(updateExamNameDTO.getOldExamName())) {
                classCircular.getTestSchedulesGrade().put(updateExamNameDTO.getNewExamName(), classCircular.getTestSchedulesGrade().remove(updateExamNameDTO.getOldExamName()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getTestSchedulesGrade().containsKey(updateExamNameDTO.getOldExamName())) {
                studentExam.getTestSchedulesGrade().put(updateExamNameDTO.getNewExamName(), studentExam.getTestSchedulesGrade().remove(updateExamNameDTO.getOldExamName()));
            }
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Test Schedule Grade name updated from " + updateExamNameDTO.getOldExamName() + " to " + updateExamNameDTO.getNewExamName() + " in both tables.");
    }

    @PutMapping("/updateExamSubjectDate/{className}")
    @Transactional
    public ResponseEntity<String> updateExamSubjectDate(@PathVariable String className, @RequestBody UpdateExamSubjectDateDTO updateExamSubjectDateDTO, @RequestParam String session) {
        //Updating class circular using exam name and session to find the circular and then updating the subject date in the exam schedule of that circular
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getExamSchedules().containsKey(updateExamSubjectDateDTO.getExamName())) {
                classCircular.getExamSchedules().get(updateExamSubjectDateDTO.getExamName())
                        .forEach(schedule -> {
                            if (schedule.getSubjectName().equalsIgnoreCase(updateExamSubjectDateDTO.getExamSchedule().getSubjectName())) {
                                schedule.setDate(updateExamSubjectDateDTO.getExamSchedule().getDate());
                                schedule.setMaxMarks(updateExamSubjectDateDTO.getExamSchedule().getMaxMarks());
                            }
                        });
                classCircularRepository.save(classCircular);
            }
        });
        //updating student exams using exam name to find the student exams and then updating the subject date in the exam schedule of that student exams
        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getExamSchedules().containsKey(updateExamSubjectDateDTO.getExamName())) {
                studentExam.getExamSchedules().get(updateExamSubjectDateDTO.getExamName())
                        .forEach(schedule -> {
                            if (schedule.getSubjectName().equalsIgnoreCase(updateExamSubjectDateDTO.getExamSchedule().getSubjectName())) {
                                //schedule.setDate(updateExamSubjectDateDTO.getExamSchedule().getDate());
                            }
                        });
            }
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Exam name " + updateExamSubjectDateDTO.getExamName() + " data updated in both tables.");
    }

    @DeleteMapping("/deleteExamSchedule/{className}")
    @Transactional
    public ResponseEntity<String> deleteExamSchedule(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
        String examKey = deleteExamNameDTO.getExamName().toLowerCase();
        // Delete from ClassCircular
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getExamSchedules() != null) {
                        Map<String, List<ExamSchedule>> examSchedules =
                                new HashMap<>(classCircular.getExamSchedules());
                        examSchedules.remove(examKey);
                        classCircular.setExamSchedules(examSchedules);
                    }
                    classCircularRepository.save(classCircular);
                });
        // Delete from StudentExams
        List<StudentExams> studentExamsList =
                studentExamsRepository.findAllByClassNameAndSession(className, session);

        studentExamsList.forEach(studentExam -> {
            if (studentExam.getExamSchedules() != null) {
                Map<String, List<StudentExamSchedule>> examSchedules =
                        new HashMap<>(studentExam.getExamSchedules());
                examSchedules.remove(examKey);
                studentExam.setExamSchedules(examSchedules);
            }
            if (studentExam.getExamScheduleComments() != null) {

                Map<String, String> comments =
                        new HashMap<>(studentExam.getExamScheduleComments());
                comments.remove(examKey);

                studentExam.setExamScheduleComments(comments);
            }
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Exam schedule " + deleteExamNameDTO.getExamName() + " deleted for class " + className + " in both ClassCircular and StudentExams.");
    }

    @DeleteMapping("/deleteTestExamSchedule/{className}")
    @Transactional
    public ResponseEntity<String> deleteTestExamSchedule(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            classCircular.getTestSchedules().remove(deleteExamNameDTO.getExamName().toLowerCase());
            classCircularRepository.save(classCircular);
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            studentExam.getTestSchedules().remove(deleteExamNameDTO.getExamName().toLowerCase());
            studentExam.getTestScheduleComments().remove(deleteExamNameDTO.getExamName().toLowerCase());
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Test Exam schedule " + deleteExamNameDTO.getExamName() + " deleted for class " + className + " in both ClassCircular and StudentExams.");
    }

    @DeleteMapping("/deleteExamScheduleGrade/{className}")
    @Transactional
    public ResponseEntity<String> deleteExamScheduleGrade(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
        String examKey = deleteExamNameDTO.getExamName().toLowerCase();
        // Delete from ClassCircular
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getExamSchedulesGrade() != null) {
                Map<String, List<ExamScheduleGrade>> examSchedules =
                        new HashMap<>(classCircular.getExamSchedulesGrade());
                examSchedules.remove(examKey);
                classCircular.setExamSchedulesGrade(examSchedules);
            }
            classCircularRepository.save(classCircular);
        });

        // Delete from StudentExams
        List<StudentExams> studentExamsList =
                studentExamsRepository.findAllByClassNameAndSession(className, session);

        studentExamsList.forEach(studentExam -> {
            if (studentExam.getExamSchedulesGrade() != null) {
                Map<String, List<StudentExamScheduleGrade>> examSchedules =
                        new HashMap<>(studentExam.getExamSchedulesGrade());
                examSchedules.remove(examKey);
                studentExam.setExamSchedulesGrade(examSchedules);
            }
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Exam schedule Grade " + deleteExamNameDTO.getExamName() + " deleted for class " + className + " in both ClassCircular and StudentExams.");
    }

    @DeleteMapping("/deleteTestScheduleGrade/{className}")
    @Transactional
    public ResponseEntity<String> deleteTestScheduleGrade(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
        String examKey = deleteExamNameDTO.getExamName().toLowerCase();
        // Delete from ClassCircular
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getTestSchedulesGrade() != null) {
                Map<String, List<ExamScheduleGrade>> examSchedules =
                        new HashMap<>(classCircular.getTestSchedulesGrade());
                examSchedules.remove(examKey);
                classCircular.setTestSchedulesGrade(examSchedules);
            }
            classCircularRepository.save(classCircular);
        });


        // Delete from StudentExams
        List<StudentExams> studentExamsList =
                studentExamsRepository.findAllByClassNameAndSession(className, session);

        studentExamsList.forEach(studentExam -> {
            if (studentExam.getTestSchedulesGrade() != null) {
                Map<String, List<StudentExamScheduleGrade>> examSchedules =
                        new HashMap<>(studentExam.getTestSchedulesGrade());
                examSchedules.remove(examKey);
                studentExam.setTestSchedulesGrade(examSchedules);
            }
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Test schedule Grade " + deleteExamNameDTO.getExamName() + " deleted for class " + className + " in both ClassCircular and StudentExams.");
    }

    @DeleteMapping("/deleteSubjectFromExamSchedule/{className}")
    @Transactional
    public ResponseEntity<String> deleteSubjectFromExamSchedule(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getExamSchedules().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
                classCircular.getExamSchedules().get(deleteExamNameDTO.getExamName().toLowerCase())
                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getExamSchedules().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
                studentExam.getExamSchedules().get(deleteExamNameDTO.getExamName().toLowerCase())
                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
                studentExamsRepository.save(studentExam);
            }
        });

        return ResponseEntity.ok("Subject " + deleteExamNameDTO.getSubjectName() + " deleted from exam schedule " + deleteExamNameDTO.getExamName() + " for class " + className + " in both tables.");
    }

    @DeleteMapping("/deleteSubjectFromTestSchedule/{className}")
    @Transactional
    public ResponseEntity<String> deleteSubjectFromTestSchedule(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getTestSchedules().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
                classCircular.getTestSchedules().get(deleteExamNameDTO.getExamName().toLowerCase())
                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getTestSchedules().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
                studentExam.getTestSchedules().get(deleteExamNameDTO.getExamName().toLowerCase())
                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
                studentExamsRepository.save(studentExam);
            }
        });

        return ResponseEntity.ok("Subject " + deleteExamNameDTO.getSubjectName() + " deleted from exam schedule " + deleteExamNameDTO.getExamName() + " for class " + className + " in both tables.");
    }

    @DeleteMapping("/deleteSubjectFromExamScheduleGrade/{className}")
    @Transactional
    public ResponseEntity<String> deleteSubjectFromExamScheduleGrade(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getExamSchedulesGrade().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
                classCircular.getExamSchedulesGrade().get(deleteExamNameDTO.getExamName().toLowerCase())
                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getExamSchedulesGrade().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
                studentExam.getExamSchedulesGrade().get(deleteExamNameDTO.getExamName().toLowerCase())
                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
                studentExamsRepository.save(studentExam);
            }
        });

        return ResponseEntity.ok("Subject " + deleteExamNameDTO.getSubjectName() + " deleted from exam schedule grade" + deleteExamNameDTO.getExamName() + " for class " + className + " in both tables.");
    }

    @DeleteMapping("/deleteSubjectFromTestScheduleGrade/{className}")
    @Transactional
    public ResponseEntity<String> deleteSubjectFromTestScheduleGrade(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getTestSchedulesGrade().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
                classCircular.getTestSchedulesGrade().get(deleteExamNameDTO.getExamName().toLowerCase())
                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getTestSchedulesGrade().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
                studentExam.getTestSchedulesGrade().get(deleteExamNameDTO.getExamName().toLowerCase())
                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
                studentExamsRepository.save(studentExam);
            }
        });

        return ResponseEntity.ok("Subject " + deleteExamNameDTO.getSubjectName() + " deleted from exam schedule grade" + deleteExamNameDTO.getExamName() + " for class " + className + " in both tables.");
    }



    //CLASS QUALITIES
    @PutMapping("/updateClassQualityName/{className}")
    @Transactional
    public ResponseEntity<String> updateClassQualityName(@PathVariable String className, @RequestBody UpdateClassQualityNameDTO updateClassQualityNameDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getClassQualities().containsKey(updateClassQualityNameDTO.getOldClassQualityName())) {
                classCircular.getClassQualities().put(updateClassQualityNameDTO.getNewClassQualityName(), classCircular.getClassQualities().remove(updateClassQualityNameDTO.getOldClassQualityName()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getClassQualities().containsKey(updateClassQualityNameDTO.getOldClassQualityName())) {
                studentExam.getClassQualities().put(updateClassQualityNameDTO.getNewClassQualityName(), studentExam.getClassQualities().remove(updateClassQualityNameDTO.getOldClassQualityName()));
            }
            if (studentExam.getClassQualities().containsKey(updateClassQualityNameDTO.getOldClassQualityName())) {
                studentExam.getClassQualities().put(updateClassQualityNameDTO.getNewClassQualityName(), studentExam.getClassQualities().remove(updateClassQualityNameDTO.getOldClassQualityName()));
            }
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Class Qualities updated from " + updateClassQualityNameDTO.getOldClassQualityName() + " to " + updateClassQualityNameDTO.getNewClassQualityName() + " in both tables.");
    }

    @DeleteMapping("/deleteClassQuality/{className}")
    @Transactional
    public ResponseEntity<String> deleteClassQuality(@PathVariable String className, @RequestBody DeleteClassQualityDTO deleteClassQualityDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            classCircular.getClassQualities().remove(deleteClassQualityDTO.getClassQualityName().toLowerCase());
            classCircularRepository.save(classCircular);
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            studentExam.getClassQualities().remove(deleteClassQualityDTO.getClassQualityName().toLowerCase());
            studentExamsRepository.save(studentExam);
        });

        return ResponseEntity.ok("Class Quality " + deleteClassQualityDTO.getClassQualityName() + " deleted for class " + className + " in both ClassCircular and StudentExams.");
    }

    @DeleteMapping("/deleteClassQualityFromClassQualities/{className}")
    @Transactional
    public ResponseEntity<String> deleteClassQualityFromClassQualities(@PathVariable String className, @RequestBody DeleteClassQualityDTO deleteClassQualityDTO, @RequestParam String session) {
        classCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
            if (classCircular.getClassQualities().containsKey(deleteClassQualityDTO.getClassQualityName().toLowerCase())) {
                classCircular.getClassQualities().get(deleteClassQualityDTO.getClassQualityName().toLowerCase())
                        .removeIf(cq -> cq.equalsIgnoreCase(deleteClassQualityDTO.getClassQuality().toLowerCase()));
                classCircularRepository.save(classCircular);
            }
        });

        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
        studentExamsList.forEach(studentExam -> {
            if (studentExam.getClassQualities().containsKey(deleteClassQualityDTO.getClassQualityName().toLowerCase())) {
                studentExam.getClassQualities().get(deleteClassQualityDTO.getClassQualityName().toLowerCase())
                        .removeIf(cq -> cq.getQualityName().equalsIgnoreCase(deleteClassQualityDTO.getClassQuality().toLowerCase()));
                studentExamsRepository.save(studentExam);
            }
        });

        return ResponseEntity.ok("Class Quality " + deleteClassQualityDTO.getClassQuality() + " deleted from class qualities " + deleteClassQualityDTO.getClassQualityName() + " for class " + className + " in both tables.");
    }
}
