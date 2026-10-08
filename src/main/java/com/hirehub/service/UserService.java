package com.hirehub.service;

import org.springframework.stereotype.Service;

import com.hirehub.dto.RegisterUserRequest;
import com.hirehub.dto.UserResponse;
import com.hirehub.entity.User;
import com.hirehub.exception.DuplicateEmailException;
import com.hirehub.repository.UserRepository;

@Service
public class UserService {

	private UserRepository userRepository;
	
	public UserService(UserRepository repository) {
		this.userRepository = repository;
	}
	
	
	public UserResponse registerUser(RegisterUserRequest request) {
		boolean result = userRepository.existsByEmail(request.getEmail());
		User user = new User();
		UserResponse response = new UserResponse();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setRole("CANDIDATE");
		if(result) {
			throw new DuplicateEmailException("Email Already Existed");
		}
		User savedUser = userRepository.save(user);
		response.setId(savedUser.getId());
		response.setName(savedUser.getName());
		response.setEmail(savedUser.getEmail());
		response.setRole(savedUser.getRole());
		response.setCreatedAt(savedUser.getCreatedAt());
		return response;
	}
}
