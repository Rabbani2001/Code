package com.schooltech.sms.controller;


import com.schooltech.security.jwt.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class SchoolManagementRestController {
    @Autowired
    private AuthService authService;
    @Value("${customProperties.coach.name}")
    private String name;

    /*
    Test to check app is working or not. An open Endpoint
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Skoolish App! " + name;
    }

    /*
    Check who is the current logged in user
     */
    @GetMapping("/logged-in-user")
    public String getLoggedInUser(Principal principal) {
        return principal.getName();
    }

}
// CAN BE USED TO HANDLE LAZY INITILIAZATION EXCEPTION
//        if(((PersistentBag) student.getTeacher().getStudents()).wasInitialized() == false){
//            student.getTeacher().setStudents(null);
//        }