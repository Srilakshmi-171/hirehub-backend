
package com.hirehub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hirehub.dto.AdminUserResponse;
import com.hirehub.entity.AccountStatus;
import com.hirehub.entity.User;
import com.hirehub.exception.UserNotFoundException;
import com.hirehub.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get all registered users
    public List<AdminUserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Get user by ID
    public AdminUserResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + userId));

        return convertToResponse(user);
    }

    // Convert User entity to AdminUserResponse DTO
    private AdminUserResponse convertToResponse(User user) {

        AdminUserResponse response = new AdminUserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setAccountStatus(user.getAccountStatus());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }
    
    public AdminUserResponse updateAccountStatus(
            Long userId, AccountStatus accountStatus) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + userId));

        user.setAccountStatus(accountStatus);

        User updatedUser = userRepository.save(user);

        return convertToResponse(updatedUser);
    }
}
