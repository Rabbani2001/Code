package com.schooltech.security.jwt.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CustomHeaderInterceptor implements HandlerInterceptor {
    //Need to check CustomHeaderInterceptor Use
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Add custom header
        // request.setAttribute("X-Custom-Header", "MyCustomValue");
        return true; // Continue request processing
    }
}
