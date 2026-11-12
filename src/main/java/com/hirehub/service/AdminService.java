
package com.hirehub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hirehub.dto.AdminJobResponse;
import com.hirehub.dto.AdminUserResponse;
import com.hirehub.entity.AccountStatus;
import com.hirehub.entity.Job;
import com.hirehub.entity.User;
import com.hirehub.exception.JobNotFoundException;
import com.hirehub.exception.UserNotFoundException;
import com.hirehub.repository.JobRepository;
import com.hirehub.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public AdminService(
            UserRepository userRepository,
            JobRepository jobRepository) {

        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
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
                .orElseThrow(() ->
                        new UserNotFoundException(
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

    // Update user account status
    public AdminUserResponse updateAccountStatus(
            Long userId, AccountStatus accountStatus) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with ID: " + userId));

        user.setAccountStatus(accountStatus);

        User updatedUser = userRepository.save(user);

        return convertToResponse(updatedUser);
    }

    // Admin: Get all jobs, including deleted jobs
    public List<AdminJobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(this::convertJobToResponse)
                .toList();
    }

    // Admin: Get job by ID, including deleted jobs
    public AdminJobResponse getJobById(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with ID: " + jobId));

        return convertJobToResponse(job);
    }

    // Admin: Soft-delete a job
    public void removeJob(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with ID: " + jobId));

        job.setDeleted(true);

        jobRepository.save(job);
    }

    // Convert Job entity to AdminJobResponse DTO
    private AdminJobResponse convertJobToResponse(Job job) {

        AdminJobResponse response = new AdminJobResponse();

        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setCompanyName(job.getCompanyName());
        response.setDescription(job.getDescription());
        response.setLocation(job.getLocation());
        response.setRequiredSkills(job.getRequiredSkills());
        response.setSalary(job.getSalary());
        response.setVacancies(job.getVacancies());
        response.setExperienceLevel(job.getExperienceLevel());
        response.setStatus(job.getStatus());
        response.setCreatedAt(job.getCreatedAt());
        response.setRecruiterId(job.getRecruiter().getId());
        response.setDeleted(job.isDeleted());

        return response;
    }
}
