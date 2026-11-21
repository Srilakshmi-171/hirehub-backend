
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class SecurityConfig {
	
	private static final Logger logger =
	        LoggerFactory.getLogger(SecurityConfig.class);

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

                    logger.warn(
                            "Access denied: {} {} | Exception: {}",
                            request.getMethod(),
                            request.getRequestURI(),
                            exception.getClass().getSimpleName()
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
                    "/api/applications/*/apply"
                ).hasRole("CANDIDATE")

                // Only recruiters can update jobs
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/jobs/**"
                ).hasRole("RECRUITER")

                // Only candidates can remove saved jobs
                // Must be before the general DELETE rule
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
                // PATCH endpoints
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/applications/**"
                ).hasRole("RECRUITER")

                // Only recruiters can update application statuses
                // PUT endpoint used by ApplicationController
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
