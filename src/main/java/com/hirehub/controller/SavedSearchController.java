
package com.hirehub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hirehub.dto.SavedSearchRequest;
import com.hirehub.dto.SavedSearchResponse;
import com.hirehub.service.SavedSearchService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/candidates/me/saved-searches")
public class SavedSearchController {

    private final SavedSearchService savedSearchService;

    public SavedSearchController(
            SavedSearchService savedSearchService) {
        this.savedSearchService = savedSearchService;
    }

    // Create a saved search
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<SavedSearchResponse> createSavedSearch(
            @Valid @RequestBody SavedSearchRequest request,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        SavedSearchResponse response =
                savedSearchService.createSavedSearch(
                        candidateId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all saved searches
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<SavedSearchResponse>> getMySavedSearches(
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                savedSearchService.getMySavedSearches(candidateId));
    }

    // Update a saved search
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{searchId}")
    public ResponseEntity<SavedSearchResponse> updateSavedSearch(
            @PathVariable Long searchId,
            @Valid @RequestBody SavedSearchRequest request,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                savedSearchService.updateSavedSearch(
                        searchId, candidateId, request));
    }

    // Delete a saved search
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{searchId}")
    public ResponseEntity<Void> deleteSavedSearch(
            @PathVariable Long searchId,
            Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        savedSearchService.deleteSavedSearch(searchId, candidateId);

        return ResponseEntity.noContent().build();
    }
}
