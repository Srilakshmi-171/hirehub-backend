package com.hirehub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF protection for API development (prevents 403 Forbidden on POST)
            .csrf(csrf -> csrf.disable())
            
            // 2. Configure endpoint authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow POST /api/users/register without authentication
                .requestMatchers("/api/users/register").permitAll()
                
                // Keep all other requests authenticated by default
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
