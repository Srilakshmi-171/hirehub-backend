
package com.hirehub.controller;

import com.hirehub.dto.UpdateApplicationStatusRequest;
import com.hirehub.entity.Application;
import com.hirehub.service.ApplicationService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
public class ApplicationStatusController {

    private final ApplicationService applicationService;

    public ApplicationStatusController(
            ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        Application application =
                applicationService.updateApplicationStatus(
                        applicationId,
                        recruiterId,
                        request.getStatus());

        return ResponseEntity.ok(
                "Application status updated to "
                        + application.getStatus());
    }
}
