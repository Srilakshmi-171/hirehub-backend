package com.hirehub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hirehub.dto.SavedJobResponse;
import com.hirehub.entity.SavedJob;
import com.hirehub.service.SavedJobService;

@RestController
@RequestMapping("/api/jobs")
public class SavedJobController {

    private final SavedJobService savedJobService;

    public SavedJobController(SavedJobService savedJobService) {
        this.savedJobService = savedJobService;
    }

    // Save a job
    @PostMapping("/{jobId}/save")
    public ResponseEntity<String> saveJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        savedJobService.saveJob(candidateId, jobId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Job saved successfully");
    }

    // Get all saved jobs
    @GetMapping("/saved")
    public ResponseEntity<List<SavedJobResponse>> getSavedJobs(
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                savedJobService.getSavedJobs(candidateId));
    }

    // Remove a saved job
    @DeleteMapping("/{jobId}/save")
    public ResponseEntity<String> removeSavedJob(
            @PathVariable Long jobId,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        savedJobService.removeSavedJob(candidateId, jobId);

        return ResponseEntity.ok("Saved job removed successfully");
    }
}