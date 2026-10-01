package com.schooltech.security.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails userDetails = User.builder().username("skn11").password(passwordEncoder().encode("skn11")).roles("ADMIN").build();
//        UserDetails userDetails1 = User.builder().username("DURGESH").password(passwordEncoder().encode("DURGESH")).roles("ADMIN").build();
//        return new InMemoryUserDetailsManager(userDetails,userDetails1);
//    }

    //This tells to password encoder to what type("BCryptPasswordEncoder") of encryption to be used.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}
