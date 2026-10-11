package com.hirehub.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hirehub.entity.User;
import com.hirehub.repository.UserRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No bearer token: let Spring Security handle authorization.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(7);

        try {
            // Validate the token and extract the user ID.
            Long userId = jwtService.extractUserId(jwtToken);

            // Find the user in the database.
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid or expired token");
                return;
            }

            User user = userOptional.get();

            // Build the user's authority from the database role.
            var authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole()));

            // Create an authenticated Spring Security object.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getId(),
                            null,
                            authorities);

            // Store authentication for this request.
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token");
        }
    }
}