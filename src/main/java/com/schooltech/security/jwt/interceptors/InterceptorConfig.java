package com.schooltech.security.jwt.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    //Need to check InterceptorConfig Use

    @Autowired
    private CustomHeaderInterceptor customHeaderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Apply the interceptor only to a specific endpoint
        registry.addInterceptor(customHeaderInterceptor)
                .addPathPatterns("/auth/login"); // Change this to your actual endpoint
    }
}
