
package com.hirehub.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hirehub.dto.ApiErrorResponse;
import com.hirehub.entity.User;
import com.hirehub.repository.UserRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository,
            ObjectMapper objectMapper) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No Bearer token: continue to Spring Security.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(7);
        Long userId;

        // Validate token and extract user ID.
        try {
            userId = jwtService.extractUserId(jwtToken);

        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            writeUnauthorizedResponse(request, response);
            return;
        }

        // Check whether the user still exists.
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            SecurityContextHolder.clearContext();
            writeUnauthorizedResponse(request, response);
            return;
        }

        User user = userOptional.get();

        // Load role from database.
        var authorities = List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole()));

        // Create authenticated principal.
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getId(),
                        null,
                        authorities);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        // Continue filter chain outside JWT exception handling.
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Invalid or expired token",
                request.getRequestURI(),
                Map.of()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponse
        );
    }
}
