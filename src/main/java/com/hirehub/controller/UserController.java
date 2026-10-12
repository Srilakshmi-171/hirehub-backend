package com.hirehub.controller;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.dto.RegisterUserRequest;
import com.hirehub.dto.UserResponse;
import com.hirehub.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {

	private UserService userService;

	public UserController(UserService service) {
		this.userService = service;
	}

	@PostMapping("/api/users/register")
	public UserResponse register(@Valid @RequestBody RegisterUserRequest request)
			throws MethodArgumentNotValidException {
		UserResponse response = userService.registerUser(request);
		return response;
	}
	
	@GetMapping("/api/users/{id}")
	public UserResponse getUser(@PathVariable Long id, Authentication authentication) throws AccessDeniedException {
		Long loggedInUserId = (Long) authentication.getPrincipal();
		boolean isAdmin = authentication.getAuthorities().stream()
		        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
		if (!loggedInUserId.equals(id) && !isAdmin) {
		    throw new AccessDeniedException("You are not allowed to access this profile");
		}
		return userService.getUserById(id);
		
	}

}
