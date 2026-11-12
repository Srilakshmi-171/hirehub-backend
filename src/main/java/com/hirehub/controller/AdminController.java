
package com.hirehub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hirehub.dto.AdminJobResponse;
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

    // Get all jobs, including deleted jobs
    @GetMapping("/jobs")
    public ResponseEntity<List<AdminJobResponse>> getAllJobs() {

        return ResponseEntity.ok(adminService.getAllJobs());
    }

    // Get job by ID, including deleted jobs
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<AdminJobResponse> getJobById(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(adminService.getJobById(jobId));
    }

    // Remove job (soft delete)
    @PatchMapping("/jobs/{jobId}/remove")
    public ResponseEntity<String> removeJob(
            @PathVariable Long jobId) {

        adminService.removeJob(jobId);

        return ResponseEntity.ok("Job removed successfully");
    }
}
