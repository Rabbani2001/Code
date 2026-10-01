package com.schooltech.sms.controller.circulars;

import com.schooltech.sms.entity.client.circulars.RoleCircular;
import com.schooltech.sms.service.circular.RoleCircularService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleCircularController {
    @Autowired
    private RoleCircularService roleCircularService;
//    @Autowired
//    private RoleCircularRepository roleCircularRepository;

//    @GetMapping("/getClassCirculars")
//    public ResponseEntity<List<ClassCircular>> getClassCirculars() {
//        return roleCircularService.getClassCirculars();
//    }

    @GetMapping("/getRoleCircularsBySession")
    public ResponseEntity<List<RoleCircular>> getRoleCircularsBySession(@RequestParam String session) {
        return roleCircularService.getRoleCircularsBySession(session);
    }

//    @GetMapping("/getClassCircularsByClassNames/{classNames}")
//    public ResponseEntity<List<ClassCircular>> getClassCircularsByClassNames(@PathVariable String classNames, @RequestParam String session) {
//        return roleCircularService.getClassCircularsByClassNames(classNames, session);
//    }

//    @GetMapping("/isClassLinked/{classNames}")
//    public ResponseEntity<Boolean> isClassLinked(@PathVariable String classNames, @RequestParam String session) {
//        return roleCircularService.isClassLinked(classNames, session);
//    }

    @PostMapping("/saveRoleCirculars")
    @Transactional
    public ResponseEntity<String> saveRoleCirculars(@RequestBody List<RoleCircular> roleCirculars) {
        return roleCircularService.saveRoleCirculars(roleCirculars);
    }

    @PutMapping("/updateRoleCirculars")
    @Transactional
    public ResponseEntity<String> updateRoleCirculars(@RequestBody List<RoleCircular> roleCirculars) {
        return roleCircularService.updateRoleCirculars(roleCirculars);
    }

//    @DeleteMapping("/deleteClassCirculars/{className}")
//    public ResponseEntity<String> deleteCLassCirculars(@PathVariable String className, @RequestParam String session) {
//        return roleCircularService.deleteClassCirculars(className, session);
//    }

    //EXAM SCHEDULE
//    @PutMapping("/updateExamName/{className}")
//    @Transactional
//    public ResponseEntity<String> updateExamName(@PathVariable String className, @RequestBody UpdateExamNameDTO updateExamNameDTO, @RequestParam String session) {
//        roleCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
//            if (classCircular.getExamSchedules().containsKey(updateExamNameDTO.getOldExamName())) {
//                classCircular.getExamSchedules().put(updateExamNameDTO.getNewExamName(), classCircular.getExamSchedules().remove(updateExamNameDTO.getOldExamName()));
//                roleCircularRepository.save(classCircular);
//            }
//        });
//
//        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
//        studentExamsList.forEach(studentExam -> {
//            if (studentExam.getExamSchedules().containsKey(updateExamNameDTO.getOldExamName())) {
//                studentExam.getExamSchedules().put(updateExamNameDTO.getNewExamName(), studentExam.getExamSchedules().remove(updateExamNameDTO.getOldExamName()));
//            }
//            if (studentExam.getExamScheduleComments().containsKey(updateExamNameDTO.getOldExamName())) {
//                studentExam.getExamScheduleComments().put(updateExamNameDTO.getNewExamName(), studentExam.getExamScheduleComments().remove(updateExamNameDTO.getOldExamName()));
//            }
//            studentExamsRepository.save(studentExam);
//        });
//
//        return ResponseEntity.ok("Exam name updated from " + updateExamNameDTO.getOldExamName() + " to " + updateExamNameDTO.getNewExamName() + " in both tables.");
//    }
//
//    @PutMapping("/updateExamSubjectDate/{className}")
//    @Transactional
//    public ResponseEntity<String> updateExamSubjectDate(@PathVariable String className, @RequestBody UpdateExamSubjectDateDTO updateExamSubjectDateDTO, @RequestParam String session) {
//        //Updating class circular using exam name and session to find the circular and then updating the subject date in the exam schedule of that circular
//        roleCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
//            if (classCircular.getExamSchedules().containsKey(updateExamSubjectDateDTO.getExamName())) {
//                classCircular.getExamSchedules().get(updateExamSubjectDateDTO.getExamName())
//                        .forEach(schedule -> {
//                            if (schedule.getSubjectName().equalsIgnoreCase(updateExamSubjectDateDTO.getExamSchedule().getSubjectName())) {
//                                schedule.setDate(updateExamSubjectDateDTO.getExamSchedule().getDate());
//                                schedule.setMaxMarks(updateExamSubjectDateDTO.getExamSchedule().getMaxMarks());
//                            }
//                        });
//                roleCircularRepository.save(classCircular);
//            }
//        });
//        //updating student exams using exam name to find the student exams and then updating the subject date in the exam schedule of that student exams
//        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
//        studentExamsList.forEach(studentExam -> {
//            if (studentExam.getExamSchedules().containsKey(updateExamSubjectDateDTO.getExamName())) {
//                studentExam.getExamSchedules().get(updateExamSubjectDateDTO.getExamName())
//                        .forEach(schedule -> {
//                            if (schedule.getSubjectName().equalsIgnoreCase(updateExamSubjectDateDTO.getExamSchedule().getSubjectName())) {
//                                //schedule.setDate(updateExamSubjectDateDTO.getExamSchedule().getDate());
//                            }
//                        });
//            }
//            studentExamsRepository.save(studentExam);
//        });
//
//        return ResponseEntity.ok("Exam name " + updateExamSubjectDateDTO.getExamName() + " data updated in both tables.");
//    }
//
//    @DeleteMapping("/deleteExamSchedule/{className}")
//    @Transactional
//    public ResponseEntity<String> deleteExamSchedule(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
//        roleCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
//            classCircular.getExamSchedules().remove(deleteExamNameDTO.getExamName().toLowerCase());
//            roleCircularRepository.save(classCircular);
//        });
//
//        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
//        studentExamsList.forEach(studentExam -> {
//            studentExam.getExamSchedules().remove(deleteExamNameDTO.getExamName().toLowerCase());
//            studentExam.getExamScheduleComments().remove(deleteExamNameDTO.getExamName().toLowerCase());
//            studentExamsRepository.save(studentExam);
//        });
//
//        return ResponseEntity.ok("Exam schedule " + deleteExamNameDTO.getExamName() + " deleted for class " + className + " in both ClassCircular and StudentExams.");
//    }
//
//    @DeleteMapping("/deleteSubjectFromExamSchedule/{className}")
//    @Transactional
//    public ResponseEntity<String> deleteSubjectFromExamSchedule(@PathVariable String className, @RequestBody DeleteExamNameDTO deleteExamNameDTO, @RequestParam String session) {
//        roleCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
//            if (classCircular.getExamSchedules().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
//                classCircular.getExamSchedules().get(deleteExamNameDTO.getExamName().toLowerCase())
//                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
//                roleCircularRepository.save(classCircular);
//            }
//        });
//
//        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
//        studentExamsList.forEach(studentExam -> {
//            if (studentExam.getExamSchedules().containsKey(deleteExamNameDTO.getExamName().toLowerCase())) {
//                studentExam.getExamSchedules().get(deleteExamNameDTO.getExamName().toLowerCase())
//                        .removeIf(schedule -> schedule.getSubjectName().equalsIgnoreCase(deleteExamNameDTO.getSubjectName().toLowerCase()));
//                studentExamsRepository.save(studentExam);
//            }
//        });
//
//        return ResponseEntity.ok("Subject " + deleteExamNameDTO.getSubjectName() + " deleted from exam schedule " + deleteExamNameDTO.getExamName() + " for class " + className + " in both tables.");
//    }
//
//    //CLASS QUALITIES
//    @PutMapping("/updateClassQualityName/{className}")
//    @Transactional
//    public ResponseEntity<String> updateClassQualityName(@PathVariable String className, @RequestBody UpdateClassQualityNameDTO updateClassQualityNameDTO, @RequestParam String session) {
//        roleCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
//            if (classCircular.getClassQualities().containsKey(updateClassQualityNameDTO.getOldClassQualityName())) {
//                classCircular.getClassQualities().put(updateClassQualityNameDTO.getNewClassQualityName(), classCircular.getClassQualities().remove(updateClassQualityNameDTO.getOldClassQualityName()));
//                roleCircularRepository.save(classCircular);
//            }
//        });
//
//        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
//        studentExamsList.forEach(studentExam -> {
//            if (studentExam.getClassQualities().containsKey(updateClassQualityNameDTO.getOldClassQualityName())) {
//                studentExam.getClassQualities().put(updateClassQualityNameDTO.getNewClassQualityName(), studentExam.getClassQualities().remove(updateClassQualityNameDTO.getOldClassQualityName()));
//            }
//            if (studentExam.getClassQualities().containsKey(updateClassQualityNameDTO.getOldClassQualityName())) {
//                studentExam.getClassQualities().put(updateClassQualityNameDTO.getNewClassQualityName(), studentExam.getClassQualities().remove(updateClassQualityNameDTO.getOldClassQualityName()));
//            }
//            studentExamsRepository.save(studentExam);
//        });
//
//        return ResponseEntity.ok("Class Qualities updated from " + updateClassQualityNameDTO.getOldClassQualityName() + " to " + updateClassQualityNameDTO.getNewClassQualityName() + " in both tables.");
//    }
//
//    @DeleteMapping("/deleteClassQuality/{className}")
//    @Transactional
//    public ResponseEntity<String> deleteClassQuality(@PathVariable String className, @RequestBody DeleteClassQualityDTO deleteClassQualityDTO, @RequestParam String session) {
//        roleCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
//            classCircular.getClassQualities().remove(deleteClassQualityDTO.getClassQualityName().toLowerCase());
//            roleCircularRepository.save(classCircular);
//        });
//
//        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
//        studentExamsList.forEach(studentExam -> {
//            studentExam.getClassQualities().remove(deleteClassQualityDTO.getClassQualityName().toLowerCase());
//            studentExamsRepository.save(studentExam);
//        });
//
//        return ResponseEntity.ok("Class Quality " + deleteClassQualityDTO.getClassQualityName() + " deleted for class " + className + " in both ClassCircular and StudentExams.");
//    }
//
//    @DeleteMapping("/deleteClassQualityFromClassQualities/{className}")
//    @Transactional
//    public ResponseEntity<String> deleteClassQualityFromClassQualities(@PathVariable String className, @RequestBody DeleteClassQualityDTO deleteClassQualityDTO, @RequestParam String session) {
//        roleCircularRepository.findByClassNameAndSession(className, session).ifPresent(classCircular -> {
//            if (classCircular.getClassQualities().containsKey(deleteClassQualityDTO.getClassQualityName().toLowerCase())) {
//                classCircular.getClassQualities().get(deleteClassQualityDTO.getClassQualityName().toLowerCase())
//                        .removeIf(cq -> cq.equalsIgnoreCase(deleteClassQualityDTO.getClassQuality().toLowerCase()));
//                roleCircularRepository.save(classCircular);
//            }
//        });
//
//        List<StudentExams> studentExamsList = studentExamsRepository.findAllByClassNameAndSession(className, session);
//        studentExamsList.forEach(studentExam -> {
//            if (studentExam.getClassQualities().containsKey(deleteClassQualityDTO.getClassQualityName().toLowerCase())) {
//                studentExam.getClassQualities().get(deleteClassQualityDTO.getClassQualityName().toLowerCase())
//                        .removeIf(cq -> cq.getQualityName().equalsIgnoreCase(deleteClassQualityDTO.getClassQuality().toLowerCase()));
//                studentExamsRepository.save(studentExam);
//            }
//        });
//
//        return ResponseEntity.ok("Class Quality " + deleteClassQualityDTO.getClassQuality() + " deleted from class qualities " + deleteClassQualityDTO.getClassQualityName() + " for class " + className + " in both tables.");
//    }
}
