
package com.hirehub.controller;

import java.util.List;

import com.hirehub.dto.InterviewResponse;
import com.hirehub.dto.ScheduleInterviewRequest;
import com.hirehub.service.InterviewService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/api/applications")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    // Recruiter schedules an interview
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{applicationId}/interviews")
    public ResponseEntity<InterviewResponse> scheduleInterview(
            @PathVariable Long applicationId,
            @Valid @RequestBody ScheduleInterviewRequest request,
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        InterviewResponse response =
                interviewService.scheduleInterview(
                        applicationId,
                        recruiterId,
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Candidate or owning recruiter views interview rounds
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{applicationId}/interviews")
    public ResponseEntity<List<InterviewResponse>> getInterviews(
            @PathVariable Long applicationId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        List<InterviewResponse> interviews =
                interviewService.getInterviewsForApplication(
                        applicationId,
                        userId);

        return ResponseEntity.ok(interviews);
    }
}
