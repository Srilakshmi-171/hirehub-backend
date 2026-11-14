
package com.hirehub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.hirehub.dto.PreferenceRequest;
import com.hirehub.dto.PreferenceResponse;
import com.hirehub.service.CandidatePreferenceService;

@RestController
@RequestMapping("/api/candidates/me/preferences")
public class CandidatePreferenceController {

    private final CandidatePreferenceService preferenceService;

    public CandidatePreferenceController(
            CandidatePreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    // Create candidate preferences
    @PostMapping
    public ResponseEntity<PreferenceResponse> createPreferences(
            @Valid @RequestBody PreferenceRequest request,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        PreferenceResponse response =
                preferenceService.createPreferences(candidateId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get candidate preferences
    @GetMapping
    public ResponseEntity<PreferenceResponse> getPreferences(
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                preferenceService.getPreferences(candidateId));
    }

    // Update candidate preferences
    @PutMapping
    public ResponseEntity<PreferenceResponse> updatePreferences(
            @Valid @RequestBody PreferenceRequest request,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                preferenceService.updatePreferences(candidateId, request));
    }
}
