package com.schooltech.security.jwt.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JWTResponse {

    private String jwtToken;

    private String username;  // it is same as userId

    //private String fullName;

    private String role;

    private String email;

    private String phoneNo;

    private String tenantId;

    private String lastLogin;

    //private String tenantCode;

    //private Map<String, String> tenantInfo;

    private Boolean isActive;

}
