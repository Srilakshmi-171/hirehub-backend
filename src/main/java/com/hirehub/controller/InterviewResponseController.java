
package com.hirehub.controller;

import com.hirehub.dto.CompleteInterviewRequest;
import com.hirehub.dto.InterviewFeedbackResponse;
import com.hirehub.dto.InterviewResponse;
import com.hirehub.dto.InterviewResponseRequest;
import com.hirehub.dto.RescheduleDecisionRequest;
import com.hirehub.dto.RescheduleInterviewRequest;
import com.hirehub.service.InterviewService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interviews")
public class InterviewResponseController {

    private final InterviewService interviewService;

    public InterviewResponseController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PatchMapping("/{interviewId}/response")
    public ResponseEntity<InterviewResponse> respondToInterview(
            @PathVariable Long interviewId,
            @Valid @RequestBody InterviewResponseRequest request,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        InterviewResponse response =
                interviewService.respondToInterview(
                        interviewId,
                        candidateId,
                        request.getResponse());

        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{interviewId}/reschedule")
    public ResponseEntity<InterviewResponse> requestReschedule(
            @PathVariable Long interviewId,
            @Valid @RequestBody RescheduleInterviewRequest request,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                interviewService.requestReschedule(
                        interviewId, candidateId, request));
    }
    
    @PatchMapping("/{interviewId}/reschedule-decision")
    public ResponseEntity<InterviewResponse> decideReschedule(
            @PathVariable Long interviewId,
            @Valid @RequestBody RescheduleDecisionRequest request,
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                interviewService.decideReschedule(
                        interviewId, recruiterId, request));
    }
    
    @PatchMapping("/{interviewId}/cancel")
    public ResponseEntity<InterviewResponse> cancelInterview(
            @PathVariable Long interviewId,
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                interviewService.cancelInterview(interviewId, recruiterId)
        );
    }
    
    @PatchMapping("/{interviewId}/complete")
    public ResponseEntity<InterviewResponse> completeInterview(
            @PathVariable Long interviewId,
            @Valid @RequestBody CompleteInterviewRequest request,
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                interviewService.completeInterview(
                        interviewId, recruiterId, request)
        );
    }
    
    @GetMapping("/{interviewId}/feedback")
    public ResponseEntity<InterviewFeedbackResponse> getInterviewFeedback(
            @PathVariable Long interviewId,
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                interviewService.getInterviewFeedback(
                        interviewId, recruiterId)
        );
    }
}
