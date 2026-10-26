
package com.hirehub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.entity.Application;
import com.hirehub.service.ApplicationService;

@RestController
@RequestMapping("/api/jobs")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/{jobId}/apply")
    public ResponseEntity<String> applyForJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        Application application =
                applicationService.applyForJob(jobId, candidateId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Application submitted successfully. Application ID: "
                        + application.getId());
    }
}
