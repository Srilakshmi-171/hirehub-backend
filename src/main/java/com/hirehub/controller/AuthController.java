package com.hirehub.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; // 1. Import it
import org.springframework.web.bind.annotation.RestController;
import com.hirehub.dto.LoginRequest;
import com.hirehub.dto.LoginResponse;
import com.hirehub.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth") // 2. Add it here as a base prefix
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // This endpoint will now be accessible at: /api/auth/login
    @PostMapping("/login") 
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
