
package com.hirehub.security;

import java.time.LocalDateTime;
import java.util.Map;

import com.hirehub.dto.ApiErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            ObjectMapper objectMapper) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
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

                // Authentication failure: 401
                .authenticationEntryPoint((request, response, exception) -> {

                    ApiErrorResponse errorResponse = new ApiErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.UNAUTHORIZED.value(),
                            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                            "Authentication required",
                            request.getRequestURI(),
                            Map.of()
                    );

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    objectMapper.writeValue(
                            response.getOutputStream(),
                            errorResponse
                    );
                })

                // Authorization failure: 403
                .accessDeniedHandler((request, response, exception) -> {

                    logger.warn(
                            "Access denied: {} {} | Exception: {}",
                            request.getMethod(),
                            request.getRequestURI(),
                            exception.getClass().getSimpleName()
                    );

                    ApiErrorResponse errorResponse = new ApiErrorResponse(
                            LocalDateTime.now(),
                            HttpStatus.FORBIDDEN.value(),
                            HttpStatus.FORBIDDEN.getReasonPhrase(),
                            "Access denied",
                            request.getRequestURI(),
                            Map.of()
                    );

                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    objectMapper.writeValue(
                            response.getOutputStream(),
                            errorResponse
                    );
                })
            )

            .authorizeHttpRequests(auth -> auth

                // Allow Spring error dispatches
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                // Swagger and OpenAPI documentation - public access
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                // Public authentication endpoints
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
                    "/api/applications/*/apply"
                ).hasRole("CANDIDATE")

                // Only recruiters can update jobs
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/jobs/**"
                ).hasRole("RECRUITER")

                // Only candidates can remove saved jobs
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

                // Application PUT endpoints
                .requestMatchers(
                    HttpMethod.PUT,
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

                // Candidate preference management
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/candidates/me/preferences"
                ).hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/candidates/me/preferences"
                ).hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/candidates/me/preferences"
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

                // Recruiters can view candidate resumes
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/*/resume"
                ).hasRole("RECRUITER")

                // Only recruiters can schedule interviews
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/applications/*/interviews"
                ).hasRole("RECRUITER")

                // Candidates and recruiters can view interviews
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/*/interviews"
                ).hasAnyRole("CANDIDATE", "RECRUITER")

                // Only candidates can respond to interviews
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/interviews/*/response"
                ).hasRole("CANDIDATE")

                // Admin endpoints
                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")

                // Interview rescheduling
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/interviews/*/reschedule"
                ).hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/interviews/*/reschedule-decision"
                ).hasRole("RECRUITER")

                // Only recruiters can cancel interviews
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/interviews/*/cancel"
                ).hasRole("RECRUITER")

                // Only recruiters can complete interviews
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/interviews/*/complete"
                ).hasRole("RECRUITER")

                // Only recruiters can view interview feedback
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/interviews/*/feedback"
                ).hasRole("RECRUITER")

                // Only candidates can view their interview feedback
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/interviews/*/candidate-feedback"
                ).hasRole("CANDIDATE")

                // Only candidates can view job recommendations
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/candidates/me/recommendations"
                ).hasRole("CANDIDATE")

                // Candidate saved searches
                .requestMatchers(
                    "/api/candidates/me/saved-searches",
                    "/api/candidates/me/saved-searches/**"
                ).hasRole("CANDIDATE")

                // Recruiter analytics
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/recruiter/analytics"
                ).hasRole("RECRUITER")

                // Recruiter reports
                .requestMatchers(
                    "/api/recruiter/reports/**"
                ).hasRole("RECRUITER")

                // Only recruiters can manage their own recruiter profiles
                .requestMatchers(
                    "/api/recruiters/me/profile"
                ).hasRole("RECRUITER")

                // All remaining endpoints require authentication
                .anyRequest().authenticated()
            )

            // Add JWT authentication filter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // Prevent duplicate servlet registration of the JWT filter
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
