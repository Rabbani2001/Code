package com.schooltech.sms.controller.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStudentOrSiblingDTO {
    //Parent
    private String parentUsername;
    private String parentType;
    private String parentName;
    private String email;
    private String phoneNo;
    //students
    private List<StudentSiblingDTO> siblingList;
}