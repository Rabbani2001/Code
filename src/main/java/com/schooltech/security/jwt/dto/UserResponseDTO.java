package com.schooltech.security.jwt.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {
    private String userType;  // teacher, staff, parent, student

    private String username;
    private String fullName;

    // teacher & staff
    private String role;
    private String employeeId;
    private List<String> classNames;

    // student
    private String className;
    private String rollNo;

    // parent
    private List<UserResponseDTO> children;
}