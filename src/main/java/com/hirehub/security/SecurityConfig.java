
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

            	    .requestMatchers(
            	        HttpMethod.POST,
            	        "/api/users/register",
            	        "/api/auth/login"
            	    ).permitAll()

            	    .requestMatchers(
            	        HttpMethod.POST,
            	        "/api/jobs"
            	    ).hasRole("RECRUITER")
            	    .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasRole("RECRUITER")
            	    .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasRole("RECRUITER")

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
