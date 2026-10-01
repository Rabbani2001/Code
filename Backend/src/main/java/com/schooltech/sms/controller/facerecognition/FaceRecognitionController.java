package com.schooltech.sms.controller.facerecognition;

import com.schooltech.sms.service.facerecognition.FaceRecognitionService;
import com.schooltech.sms.service.teacher.TeacherAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FaceRecognitionController {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @Autowired
    private TeacherAttendanceService teacherAttendanceService;

    @PostMapping("/attendance/face")
    public ResponseEntity<?> markAttendance(@RequestBody Map<String, String> body) {

        String base64Image = body.get("image");

        String teacher = faceRecognitionService.identifyFace(base64Image);

        if (teacher != null) {
//            List<TeacherAttendance> list = new ArrayList<>();
//            TeacherAttendance teacherAttendance = new TeacherAttendance();
//            teacherAttendance.setUsername("testteacher1");
//            teacherAttendance.setStatus(TeacherAttendance.Status.present);
//            teacherAttendance.setDate(java.time.LocalDate.now());
//            list.add(teacherAttendance);
//            teacherAttendanceService.markAttendenceTeacher(list);
            return ResponseEntity.ok(teacher);
        }

        return ResponseEntity.status(401).body("Face not recognized");
    }
}
