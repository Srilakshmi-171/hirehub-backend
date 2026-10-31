
package com.hirehub.controller;

import java.util.List;

import com.hirehub.dto.ApplicationResponse;
import com.hirehub.entity.Application;
import com.hirehub.service.ApplicationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // Apply for a job
    @PostMapping("/{jobId}/apply")
    public ResponseEntity<String> applyForJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        Long candidateId =
                (Long) authentication.getPrincipal();

        Application application =
                applicationService.applyForJob(
                        jobId,
                        candidateId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Application submitted successfully. Application ID: "
                        + application.getId());
    }

    // View applications belonging to the logged-in candidate
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            Authentication authentication) {

        Long candidateId =
                (Long) authentication.getPrincipal();

        List<ApplicationResponse> applications =
                applicationService.getMyApplications(candidateId);

        return ResponseEntity.ok(applications);
    }
}
