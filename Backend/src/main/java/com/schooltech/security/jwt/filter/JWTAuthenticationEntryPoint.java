package com.schooltech.security.jwt.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 JWTAuthenticationEntryPoint that implement AuthenticationEntryPoint.
 Method of this class is called whenever as exception is thrown due to unauthenticated
 user trying to access the resource that required authentication
 */
@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private Logger logger = LoggerFactory.getLogger(JWTAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //PrintWriter writer = response.getWriter();
        //writer.println("Access Denied !! " + authException.getMessage());
        logger.info("SchoolTech....Access Denied! " + authException.getMessage());
    }
}
