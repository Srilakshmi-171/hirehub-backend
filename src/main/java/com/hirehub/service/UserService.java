package com.hirehub.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hirehub.dto.RegisterUserRequest;
import com.hirehub.dto.UserResponse;
import com.hirehub.entity.User;
import com.hirehub.exception.DuplicateEmailException;
import com.hirehub.exception.UserNotFoundException;
import com.hirehub.repository.UserRepository;

@Service
public class UserService {

	private UserRepository userRepository;

	public UserService(UserRepository repository) {
		this.userRepository = repository;
	}

	public UserResponse registerUser(RegisterUserRequest request) {
		boolean result = userRepository.existsByEmail(request.getEmail());
		if (result) {
			throw new DuplicateEmailException("Email Already Existed");
		}
		User user = new User();
		UserResponse response = new UserResponse();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setRole("CANDIDATE");

		User savedUser = userRepository.save(user);
		response.setId(savedUser.getId());
		response.setName(savedUser.getName());
		response.setEmail(savedUser.getEmail());
		response.setRole(savedUser.getRole());
		response.setCreatedAt(savedUser.getCreatedAt());
		return response;
	}
	
	public UserResponse getUserById(Long id) {
	    // Step 1: Retrieve the User or throw UserNotFoundException
		User user = userRepository.findById(id)
			    .orElseThrow(
			        () -> new UserNotFoundException("User not found with ID: " + id)
			    );
		// Step 2: Create a UserResponse DTO
		UserResponse response = new UserResponse();
	    // Step 3: Copy safe fields from User to UserResponse
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setId(user.getId());
		response.setRole(user.getRole());
		response.setCreatedAt(user.getCreatedAt());
	    // Step 4: Return UserResponse
		return response;
	}
}
