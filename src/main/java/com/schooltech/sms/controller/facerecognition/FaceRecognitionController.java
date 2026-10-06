package com.schooltech.sms.controller.facerecognition;

import com.schooltech.sms.service.facerecognition.FaceRecognitionService;
import com.schooltech.sms.service.teacher.TeacherAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class FaceRecognitionController {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @Autowired
    private TeacherAttendanceService teacherAttendanceService;

    @PostMapping(value = "/attendance/face", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> markAttendance(@RequestParam("image") MultipartFile image) {

        Map<String, Object> result = faceRecognitionService.identifyFace(image);

        Boolean recognized = (Boolean) result.get("recognized");

        if (Boolean.TRUE.equals(recognized)) {

//            Object teacherId = result.get("teacher_id");

            return ResponseEntity.ok(result);
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(result);
    }


    @PostMapping(
            value = "/teacher/face",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> registerTeacherFace(
            @RequestParam("username") String username,
            @RequestParam("image") MultipartFile image) {

        Map<String, Object> result =
                faceRecognitionService.saveOrUpdateEmbedding(
                        username,
                        image
                );

        return ResponseEntity.ok(result);
    }
}

//    @PostMapping("/attendance/face")
//    public ResponseEntity<?> markAttendance(@RequestBody Map<String, String> body) {
//
//        String base64Image = body.get("image");
//
//        String teacher = faceRecognitionService.identifyFace(base64Image);
//
//        if (teacher != null) {
////            List<TeacherAttendance> list = new ArrayList<>();
////            TeacherAttendance teacherAttendance = new TeacherAttendance();
////            teacherAttendance.setUsername("testteacher1");
////            teacherAttendance.setStatus(TeacherAttendance.Status.present);
////            teacherAttendance.setDate(java.time.LocalDate.now());
////            list.add(teacherAttendance);
////            teacherAttendanceService.markAttendenceTeacher(list);
//            return ResponseEntity.ok(teacher);
//        }
//
//        return ResponseEntity.status(401).body("Face not recognized");
//    }
//}
