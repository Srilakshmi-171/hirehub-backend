
package com.hirehub.service;

import com.hirehub.entity.AccountStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hirehub.dto.LoginRequest;
import com.hirehub.dto.LoginResponse;
import com.hirehub.entity.User;
import com.hirehub.repository.UserRepository;
import com.hirehub.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException(
                        "Invalid email or password"));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadCredentialsException(
                    "Your account is inactive. Please contact the administrator.");
        }

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setToken(token);

        return response;
    }
}
