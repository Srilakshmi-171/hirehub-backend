
package com.hirehub.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.DispatcherType;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, exception) -> {
                    response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Authentication required"
                    );
                })
                .accessDeniedHandler((request, response, exception) -> {
                    System.out.println(
                        "ACCESS DENIED: " + request.getMethod()
                        + " " + request.getRequestURI()
                    );

                    System.out.println(
                        "Exception: " + exception.getClass().getName()
                    );

                    response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Access denied"
                    );
                })
            )

            .authorizeHttpRequests(auth -> auth

                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                // Public endpoints
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/users/register",
                    "/api/auth/login"
                ).permitAll()

                // Only recruiters can create jobs
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/jobs"
                ).hasRole("RECRUITER")

                // Only candidates can apply for jobs
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/jobs/*/apply"
                ).hasRole("CANDIDATE")

                // Only recruiters can update jobs
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/jobs/**"
                ).hasRole("RECRUITER")

                // Only candidates can remove saved jobs
                // IMPORTANT: Must be before the general DELETE rule
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/jobs/*/save"
                ).hasRole("CANDIDATE")

                // Only recruiters can delete jobs
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/jobs/**"
                ).hasRole("RECRUITER")

                // Only recruiters can update application statuses
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/applications/**"
                ).hasRole("RECRUITER")

                // Only candidates can withdraw applications
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/applications/**"
                ).hasRole("CANDIDATE")

                // Only recruiters can view applicants
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/jobs/*/applications"
                ).hasRole("RECRUITER")

                // Recruiter dashboard
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/recruiter/dashboard"
                ).hasRole("RECRUITER")

                // Candidate profile update
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/candidates/me"
                ).hasRole("CANDIDATE")
                
             // Candidate resume management
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/candidates/me/resume"
                ).hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/candidates/me/resume",
                    "/api/candidates/me/resume/download"
                ).hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/candidates/me/resume"
                ).hasRole("CANDIDATE")
                
                .requestMatchers(
                	    HttpMethod.GET,
                	    "/api/applications/*/resume"
                	).hasRole("RECRUITER")
                
             // Only recruiters can schedule interviews
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/applications/*/interviews"
                ).hasRole("RECRUITER")

                // Candidates and recruiters must be authenticated to view interviews
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/*/interviews"
                ).hasAnyRole("CANDIDATE", "RECRUITER")
                
                .requestMatchers(
                	    HttpMethod.PATCH,
                	    "/api/interviews/*/response"
                	).hasRole("CANDIDATE")
                
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.PATCH, "/api/interviews/*/reschedule")
                .hasRole("CANDIDATE")
                
                .requestMatchers(HttpMethod.PATCH,
                        "/api/interviews/*/reschedule-decision")
                .hasRole("RECRUITER")
                        
                .requestMatchers(HttpMethod.PATCH, "/api/interviews/*/cancel")
                .hasRole("RECRUITER")
                
                .requestMatchers(HttpMethod.PATCH, "/api/interviews/*/complete")
                .hasRole("RECRUITER")
                
                .requestMatchers(HttpMethod.GET, "/api/interviews/*/feedback")
                .hasRole("RECRUITER")

                // All remaining endpoints require authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
            jwtFilterServletRegistration(
                    JwtAuthenticationFilter filter) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);
        return registration;
    }
}
