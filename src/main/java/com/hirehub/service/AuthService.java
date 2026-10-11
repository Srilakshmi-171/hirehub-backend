package com.hirehub.service;

import java.util.Optional;

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

    // Constructor Injection for dependencies
    public AuthService(
        UserRepository userRepository, 
        PasswordEncoder passwordEncoder, 
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    public LoginResponse login(LoginRequest request) {
        // Implement the login flow here
    	
    	User user = userRepository.findByEmail(request.getEmail())
    		    .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
    	
    	if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    	    throw new BadCredentialsException("Invalid email or password");
    	}
    	
    	String result = jwtService.generateToken(user);
    	LoginResponse response = new LoginResponse();
    	response.setToken(result);
    	
    	return response;

    }
}
