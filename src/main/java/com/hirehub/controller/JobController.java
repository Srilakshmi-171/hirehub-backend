
package com.hirehub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.dto.CreateJobRequest;
import com.hirehub.dto.JobResponse;
import com.hirehub.dto.UpdateJobRequest;
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
    
    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(
            @PathVariable Long id) {

        Job job = jobService.getJobById(id);

        JobResponse response = new JobResponse();
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

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody UpdateJobRequest request,
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        Job updatedJob = jobService.updateJob(id, request, recruiterId);

        JobResponse response = new JobResponse();
        response.setId(updatedJob.getId());
        response.setTitle(updatedJob.getTitle());
        response.setCompanyName(updatedJob.getCompanyName());
        response.setDescription(updatedJob.getDescription());
        response.setLocation(updatedJob.getLocation());
        response.setRequiredSkills(updatedJob.getRequiredSkills());
        response.setSalary(updatedJob.getSalary());
        response.setVacancies(updatedJob.getVacancies());
        response.setExperienceLevel(updatedJob.getExperienceLevel());
        response.setStatus(updatedJob.getStatus());
        response.setCreatedAt(updatedJob.getCreatedAt());
        response.setRecruiterId(updatedJob.getRecruiter().getId());

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        jobService.deleteJob(id, recruiterId);

        return ResponseEntity.noContent().build();
    }
}
