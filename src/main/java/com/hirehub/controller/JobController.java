
package com.hirehub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.dto.CreateJobRequest;
import com.hirehub.dto.JobResponse;
import com.hirehub.entity.Job;
import com.hirehub.entity.User;
import com.hirehub.repository.UserRepository;
import com.hirehub.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;

    public JobController(
            JobService jobService,
            UserRepository userRepository) {
        this.jobService = jobService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody CreateJobRequest request,
            Authentication authentication) {

        // The JWT filter sets the authenticated user's ID as the principal.
        Long recruiterId = (Long) authentication.getPrincipal();

        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated recruiter not found"));

        Job savedJob = jobService.createJob(request, recruiter);

        JobResponse response = new JobResponse();
        response.setId(savedJob.getId());
        response.setTitle(savedJob.getTitle());
        response.setCompanyName(savedJob.getCompanyName());
        response.setDescription(savedJob.getDescription());
        response.setLocation(savedJob.getLocation());
        response.setRequiredSkills(savedJob.getRequiredSkills());
        response.setSalary(savedJob.getSalary());
        response.setVacancies(savedJob.getVacancies());
        response.setExperienceLevel(savedJob.getExperienceLevel());
        response.setStatus(savedJob.getStatus());
        response.setCreatedAt(savedJob.getCreatedAt());
        response.setRecruiterId(savedJob.getRecruiter().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
