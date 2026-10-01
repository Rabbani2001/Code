package com.schooltech.security.jwt.entity;

//public class LoginRequest {
//    private String username;
//    private String password;
//
//    // Getters and Setters
//}

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequest {
    private String username;
    private String otp;

    // Getters and Setters
}

//public class JwtResponse {
//    private String token;
//
//    public JwtResponse(String token) {
//        this.token = token;
//    }
//
//    public String getToken() {
//        return token;
//    }
//}
