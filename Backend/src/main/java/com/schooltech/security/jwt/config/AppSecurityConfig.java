package com.schooltech.security.jwt.config;


import com.schooltech.security.jwt.filter.JWTAuthenticationEntryPoint;
import com.schooltech.security.jwt.filter.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class AppSecurityConfig {
    @Autowired
    private JWTAuthenticationEntryPoint point;
    @Autowired
    private JWTAuthenticationFilter filter;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${whitelisted.url}")
    private String whitelistedURLsForCORS;


    //@Autowired
    //private TenantFilter tenantFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        //http.cors(cors -> cors.disable());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.authorizeHttpRequests(auth ->
                auth
                        //.requestMatchers("/hello/**").authenticated()
                        //.requestMatchers("/hello").hasRole("EMPLOYEE")
                        //.requestMatchers("/auth/create-user").permitAll()
                        //.requestMatchers("/auth/save-tenant").permitAll()
                        //.requestMatchers("/auth/get-tenant/*").permitAll()
                        //.requestMatchers("/getStudents/ans1").permitAll()
                        .requestMatchers("/hello").permitAll()
                        .requestMatchers("/auth/users/*").permitAll()
                        .requestMatchers("/send-email").permitAll()
                        .requestMatchers("/send-otp-via-email").permitAll()
                        .requestMatchers("/verify-email-otp").permitAll()
                        .requestMatchers("/send-otp-via-sms").permitAll()
                        .requestMatchers("/send-otp-via-sms-disabled").permitAll()
                        .requestMatchers("/verify-otp-via-sms").permitAll()
                        .requestMatchers("/auth/update-user-with-sms-otp").permitAll()
                        .requestMatchers("/auth/update-user-with-sms-otp-disabled").permitAll()
                        .requestMatchers("/auth/update-user-with-email-otp").permitAll() //Permitting user to access this resource without any JWT Authentication
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/actuator/*").permitAll()
                        .requestMatchers("/saveVisitor").permitAll()
                        .requestMatchers(
                                "/docs",
                                "/swagger-ui/**",
                                "/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
        );

        http.exceptionHandling(ex -> ex.authenticationEntryPoint(point));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //Stateless means we are not storing anything on server
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);//http.addFilterBefore(new TenantFilter(), JWTAuthenticationFilter.class);//this commented cod not working hence cretaed this manuallly in JWTAuthenticationFilter.class
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(whitelistedURLsForCORS.split(",")));//"http://192.168.1.15:5173"
        //configuration.setAllowedOrigins(List.of("/**")); // ✅ Allow React Native Web
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }
}
