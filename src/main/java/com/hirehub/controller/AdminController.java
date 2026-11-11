package com.hirehub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hirehub.dto.AdminUserResponse;
import com.hirehub.dto.UpdateAccountStatusRequest;
import com.hirehub.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Get all users
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // Get user by ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserResponse> getUserById(
            @PathVariable Long userId) {

        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    // Activate or deactivate user
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<AdminUserResponse> updateAccountStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateAccountStatusRequest request) {

        return ResponseEntity.ok(
                adminService.updateAccountStatus(
                        userId, request.getAccountStatus()));
    }
}