package com.schooltech.sms.controller.fileupload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schooltech.security.jwt.service.TenantService;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.fileupload.dto.RemoveDocumentDTO;
import com.schooltech.sms.controller.fileupload.dto.UploadDocumentDTO;
import com.schooltech.sms.entity.client.staff.Staff;
import com.schooltech.sms.entity.client.student.Parent;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.teacher.Teacher;
import com.schooltech.sms.entity.master.SchoolTenant;
import com.schooltech.sms.service.file.FileUploadS3Service;
import com.schooltech.sms.service.staff.StaffService;
import com.schooltech.sms.service.student.ParentService;
import com.schooltech.sms.service.student.StudentService;
import com.schooltech.sms.service.teacher.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.schooltech.sms.constant.AppConstant.STAFF_ELIGIBLE_ROLES;

@RestController
public class DocumentFileController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentFileController.class);
    @Autowired
    private FileUploadS3Service fileUploadS3Service;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ParentService parentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private TenantService tenantService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/uploadDocuments/{username}")
    @Transactional
    public ResponseEntity<String> uploadDocuments(@PathVariable String username, @RequestParam("uploadDocumentParams") String uploadDocumentParams, @RequestParam("file") MultipartFile file) {
        String uploadedDocUrl = null;
        try {
            UploadDocumentDTO uploadDocumentDTO = objectMapper.readValue(uploadDocumentParams, UploadDocumentDTO.class);
            //write code to update new url in the teacher teacher_document column
            if (uploadDocumentDTO.getRole() != null) {
                if (uploadDocumentDTO.getRole().equalsIgnoreCase(AppConstant.TEACHER_ROLE)) {
                    uploadedDocUrl = processS3UploadDelete(file, uploadDocumentDTO, username, AppConstant.TEACHER_ROLE.toLowerCase());

                    Teacher teacher = teacherService.getTeacherByUsername(username);
                    Map<String, String> teacherDocuments = teacher.getDocuments();
                    teacherDocuments.put(uploadDocumentDTO.getFileNameToUpload(), uploadedDocUrl);
                    teacher.setDocuments(teacherDocuments);
                    List<Teacher> teacherList = new ArrayList<>();
                    teacherList.add(teacher);
                    teacherService.updateTeachers(teacherList);
                } else if (uploadDocumentDTO.getRole().equalsIgnoreCase(AppConstant.STUDENT_ROLE)) {
                    uploadedDocUrl = processS3UploadDelete(file, uploadDocumentDTO, username, AppConstant.STUDENT_ROLE.toLowerCase());

                    Student student = studentService.getStudentUsername(username);
                    Map<String, String> studentDocuments = student.getDocuments();
                    studentDocuments.put(uploadDocumentDTO.getFileNameToUpload(), uploadedDocUrl);
                    student.setDocuments(studentDocuments);
                    List<Student> studentList = new ArrayList<>();
                    studentList.add(student);
                    studentService.updateStudents(studentList);
                } else if (STAFF_ELIGIBLE_ROLES.contains(uploadDocumentDTO.getRole())) {
                    uploadedDocUrl = processS3UploadDelete(file, uploadDocumentDTO, username, uploadDocumentDTO.getRole());

                    Staff staff = staffService.getStaffByUsername(username);
                    Map<String, String> staffDocuments = staff.getDocuments();
                    staffDocuments.put(uploadDocumentDTO.getFileNameToUpload(), uploadedDocUrl);
                    staff.setDocuments((staffDocuments));
                    List<Staff> staffList = new ArrayList<>();
                    staffList.add(staff);
                    staffService.updateStaffs(staffList);
                } else if (uploadDocumentDTO.getRole().equalsIgnoreCase(AppConstant.PARENT_ROLE)) {
                    uploadedDocUrl = processS3UploadDelete(file, uploadDocumentDTO, username, AppConstant.PARENT_ROLE.toLowerCase());

                    Parent parent = parentService.getParentByUsername(username);
                    Map<String, String> parentDocuments = parent.getDocuments();
                    parentDocuments.put(uploadDocumentDTO.getFileNameToUpload(), uploadedDocUrl);
                    parent.setDocuments(parentDocuments);
                    List<Parent> parentList = new ArrayList<>();
                    parentList.add(parent);
                    parentService.updateParents(parentList);
                }
            }
            if (uploadedDocUrl == null)
                return ResponseEntity.status(500).body("Upload Document failed as passed role not found");
            return ResponseEntity.ok(uploadedDocUrl);
        } catch (Exception e) {
            if (uploadedDocUrl != null) fileUploadS3Service.deleteFileByUrl(uploadedDocUrl);
            return ResponseEntity.status(500).body("Upload Document failed: " + e.getMessage());
        }
    }

    private String processS3UploadDelete(MultipartFile file, UploadDocumentDTO uploadDocumentDTO, String username, String role) throws IOException {
        String s3Url = fileUploadS3Service.uploadFile(file, uploadDocumentDTO.getTenantId() + "/docs/" + role + "/" + username + "/");
        // 2. If old image URL exists, delete it safely
        if (uploadDocumentDTO.getFileOldUrl() != null && !uploadDocumentDTO.getFileOldUrl().trim().isEmpty()) {
            fileUploadS3Service.deleteFileByUrl(uploadDocumentDTO.getFileOldUrl());
        }
        return s3Url;
    }


    @PostMapping("/uploadSchoolDocuments")
    @Transactional
    public ResponseEntity<String> uploadSchoolDocuments(@RequestParam("uploadDocumentParams") String uploadDocumentParams, @RequestParam("file") MultipartFile file) {
        String uploadedDocUrl = null;
        try {
            UploadDocumentDTO uploadDocumentDTO = objectMapper.readValue(uploadDocumentParams, UploadDocumentDTO.class);
            uploadedDocUrl = fileUploadS3Service.uploadFile(file, uploadDocumentDTO.getTenantId() + "/miscellaneous/");

            SchoolTenant schoolTenant = tenantService.getSchoolTenantByTenantId(uploadDocumentDTO.getTenantId());
            schoolTenant.getTenantInfo().put(uploadDocumentDTO.getFileNameToUpload(), uploadedDocUrl);
            tenantService.updateSchoolTenantByTenantId(schoolTenant);
            // 2. If old image URL exists, delete it safely
            if (uploadDocumentDTO.getFileOldUrl() != null && !uploadDocumentDTO.getFileOldUrl().trim().isEmpty()) {
                fileUploadS3Service.deleteFileByUrl(uploadDocumentDTO.getFileOldUrl());
            }

            if (uploadedDocUrl == null)
                return ResponseEntity.status(500).body("Upload School Document failed as passed role not found");
            return ResponseEntity.ok(uploadedDocUrl);
        } catch (Exception e) {
            if (uploadedDocUrl != null) fileUploadS3Service.deleteFileByUrl(uploadedDocUrl);
            return ResponseEntity.status(500).body("Upload School Document failed: " + e.getMessage());
        }
    }


    @PostMapping("/uploadResultDocuments/{username}")
    @Transactional
    public ResponseEntity<String> uploadResultDocuments(@PathVariable String username, @RequestParam("uploadDocumentParams") String uploadDocumentParams, @RequestParam("file") MultipartFile file) {
        String uploadedDocUrl = null;
        try {
            UploadDocumentDTO uploadDocumentDTO = objectMapper.readValue(uploadDocumentParams, UploadDocumentDTO.class);
            //write code to update new url in the teacher teacher_document column
            if (uploadDocumentDTO.getRole() != null) {
                if (uploadDocumentDTO.getRole().equalsIgnoreCase(AppConstant.STUDENT_ROLE)) {
                    uploadedDocUrl = fileUploadS3Service.uploadFile(file, uploadDocumentDTO.getTenantId() + "/docs/" + AppConstant.STUDENT_ROLE + "/" + username + "/result/" + uploadDocumentDTO.getSession() + "/");

                    Student student = studentService.getStudentUsername(username);
                    Map<String, String> studentDocuments = student.getDocuments();
                    studentDocuments.put(uploadDocumentDTO.getFileNameToUpload(), uploadedDocUrl);
                    student.setDocuments(studentDocuments);
                    List<Student> studentList = new ArrayList<>();
                    studentList.add(student);
                    studentService.updateStudents(studentList);

                    // 2. If old image URL exists, delete it safely
                    if (uploadDocumentDTO.getFileOldUrl() != null && !uploadDocumentDTO.getFileOldUrl().trim().isEmpty()) {
                        fileUploadS3Service.deleteFileByUrl(uploadDocumentDTO.getFileOldUrl());
                    }
                }
            }
            if (uploadedDocUrl == null)
                return ResponseEntity.status(500).body("Upload Result Document failed as passed role not found");
            return ResponseEntity.ok(uploadedDocUrl);
        } catch (Exception e) {
            if (uploadedDocUrl != null) fileUploadS3Service.deleteFileByUrl(uploadedDocUrl);
            return ResponseEntity.status(500).body("Upload Result Document failed: " + e.getMessage());
        }
    }

    @PostMapping("/uploadAdmissionDocuments/{username}")
    @Transactional
    public ResponseEntity<String> uploadAdmissionDocuments(@PathVariable String username, @RequestParam("uploadDocumentParams") String uploadDocumentParams, @RequestParam("file") MultipartFile file) {
        String uploadedDocUrl = null;
        try {
            UploadDocumentDTO uploadDocumentDTO = objectMapper.readValue(uploadDocumentParams, UploadDocumentDTO.class);
            //write code to update new url in the teacher teacher_document column
            if (uploadDocumentDTO.getRole() != null) {
                if (uploadDocumentDTO.getRole().equalsIgnoreCase(AppConstant.STUDENT_ROLE)) {
                    uploadedDocUrl = fileUploadS3Service.uploadFile(file, uploadDocumentDTO.getTenantId() + "/docs/" + AppConstant.STUDENT_ROLE + "/" + username + "/admission-form/");

                    Student student = studentService.getStudentUsername(username);
                    Map<String, String> studentDocuments = student.getDocuments();
                    studentDocuments.put(uploadDocumentDTO.getFileNameToUpload(), uploadedDocUrl);
                    student.setDocuments(studentDocuments);
                    List<Student> studentList = new ArrayList<>();
                    studentList.add(student);
                    studentService.updateStudents(studentList);

                    // 2. If old image URL exists, delete it safely
                    if (uploadDocumentDTO.getFileOldUrl() != null && !uploadDocumentDTO.getFileOldUrl().trim().isEmpty()) {
                        fileUploadS3Service.deleteFileByUrl(uploadDocumentDTO.getFileOldUrl());
                    }
                }
            }
            if (uploadedDocUrl == null)
                return ResponseEntity.status(500).body("Upload Admission Document failed as passed role not found");
            return ResponseEntity.ok(uploadedDocUrl);
        } catch (Exception e) {
            if (uploadedDocUrl != null) fileUploadS3Service.deleteFileByUrl(uploadedDocUrl);
            return ResponseEntity.status(500).body("Upload Admission Document failed: " + e.getMessage());
        }
    }

    @PostMapping("/removeProfilePhoto/{username}")
    @Transactional
    public ResponseEntity<String> removeProfilePhoto(@PathVariable String username, @RequestParam("removeDocumentParams") String removeDocumentParams) {
        try {
            RemoveDocumentDTO removeDocumentDTO = objectMapper.readValue(removeDocumentParams, RemoveDocumentDTO.class);
            // 1. If old image URL exists, delete it safely
            fileUploadS3Service.deleteFileByUrl(removeDocumentDTO.getFileOldUrl());
            if (removeDocumentDTO.getRole() != null) {
                if (removeDocumentDTO.getRole().equalsIgnoreCase(AppConstant.TEACHER_ROLE))
                    removeDocTeacherData(username, removeDocumentDTO.getFileNameToRemove());
                else if (removeDocumentDTO.getRole().equalsIgnoreCase(AppConstant.PARENT_ROLE))
                    removeDocParentData(username, removeDocumentDTO.getFileNameToRemove());
                else if (removeDocumentDTO.getRole().equalsIgnoreCase(AppConstant.STUDENT_ROLE))
                    removeDocStudentData(username, removeDocumentDTO.getFileNameToRemove());
                else if (STAFF_ELIGIBLE_ROLES.contains(removeDocumentDTO.getRole()))
                    removeDocAdminData(username, removeDocumentDTO.getFileNameToRemove());
            }
            //teacherService.updateTeacherPhoto(username, null);
            return ResponseEntity.ok("Image Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Image Delete failed: " + e.getMessage());
        }
    }

    private void removeDocTeacherData(String username, String fileName) {
        // 3. Update Student table with new image URL
        Teacher teacher = teacherService.getTeacherByUsername(username);
        Map<String, String> teacherDocuments = teacher.getDocuments();
        teacherDocuments.remove(fileName);

        teacher.setDocuments(teacherDocuments);
        List<Teacher> teacherList = new ArrayList<>();
        teacherList.add(teacher);

        teacherService.updateTeachers(teacherList);
    }

    private void removeDocAdminData(String username, String fileName) {
        // 3. Update Student table with new image URL
        Staff staff = staffService.getStaffByUsername(username);
        Map<String, String> staffDocuments = staff.getDocuments();
        staffDocuments.remove(fileName);

        staff.setDocuments((staffDocuments));
        List<Staff> staffList = new ArrayList<>();
        staffList.add(staff);

        staffService.updateStaffs(staffList);
    }

    private void removeDocStudentData(String username, String fileName) {
        // 3. Update Student table with new image URL
        Student student = studentService.getStudentUsername(username);
        Map<String, String> studentDocuments = student.getDocuments();
        studentDocuments.remove(fileName);

        student.setDocuments((studentDocuments));
        List<Student> studentList = new ArrayList<>();
        studentList.add(student);

        studentService.updateStudents(studentList);
    }

    private void removeDocParentData(String username, String fileName) {
        // 3. Update Student table with new image URL
        Parent parent = parentService.getParentByUsername(username);
        Map<String, String> parentDocuments = parent.getDocuments();
        parentDocuments.remove(fileName);

        parent.setDocuments((parentDocuments));
        List<Parent> parentList = new ArrayList<>();
        parentList.add(parent);

        parentService.updateParents(parentList);
    }
}
