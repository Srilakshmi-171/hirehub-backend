
package com.hirehub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.dto.SavedSearchRequest;
import com.hirehub.dto.SavedSearchResponse;
import com.hirehub.entity.AccountStatus;
import com.hirehub.entity.SavedSearch;
import com.hirehub.entity.User;
import com.hirehub.exception.ForbiddenAccessException;
import com.hirehub.exception.SavedSearchNotFoundException;
import com.hirehub.repository.SavedSearchRepository;
import com.hirehub.repository.UserRepository;

@Service
public class SavedSearchService {

    private final SavedSearchRepository savedSearchRepository;
    private final UserRepository userRepository;

    public SavedSearchService(
            SavedSearchRepository savedSearchRepository,
            UserRepository userRepository) {

        this.savedSearchRepository = savedSearchRepository;
        this.userRepository = userRepository;
    }

    // Create a saved search
    @Transactional
    public SavedSearchResponse createSavedSearch(
            Long candidateId,
            SavedSearchRequest request) {

        User candidate = getCandidate(candidateId);

        String keyword = normalize(request.getKeyword());
        String location = normalize(request.getLocation());

        validateSearchCriteria(keyword, location);

        SavedSearch savedSearch = new SavedSearch();

        savedSearch.setCandidate(candidate);
        savedSearch.setKeyword(keyword);
        savedSearch.setLocation(location);

        return mapToResponse(
                savedSearchRepository.save(savedSearch));
    }

    // Get all saved searches belonging to a candidate
    @Transactional(readOnly = true)
    public List<SavedSearchResponse> getMySavedSearches(
            Long candidateId) {

        getCandidate(candidateId);

        return savedSearchRepository
                .findByCandidateIdOrderByCreatedAtDesc(candidateId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Update a saved search
    @Transactional
    public SavedSearchResponse updateSavedSearch(
            Long searchId,
            Long candidateId,
            SavedSearchRequest request) {

        getCandidate(candidateId);

        SavedSearch savedSearch = savedSearchRepository
                .findByIdAndCandidateId(searchId, candidateId)
                .orElseThrow(() ->
                        new SavedSearchNotFoundException(
                                "Saved search not found"));

        String keyword = normalize(request.getKeyword());
        String location = normalize(request.getLocation());

        validateSearchCriteria(keyword, location);

        savedSearch.setKeyword(keyword);
        savedSearch.setLocation(location);

        return mapToResponse(
                savedSearchRepository.saveAndFlush(savedSearch));
    }

    // Delete a saved search
    @Transactional
    public void deleteSavedSearch(
            Long searchId,
            Long candidateId) {

        getCandidate(candidateId);

        SavedSearch savedSearch = savedSearchRepository
                .findByIdAndCandidateId(searchId, candidateId)
                .orElseThrow(() ->
                        new SavedSearchNotFoundException(
                                "Saved search not found"));

        savedSearchRepository.delete(savedSearch);
    }

    // Validate candidate account
    private User getCandidate(Long candidateId) {

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Candidate not found"));

        if (!"CANDIDATE".equals(candidate.getRole())
                || candidate.getAccountStatus() != AccountStatus.ACTIVE) {

            throw new ForbiddenAccessException(
                    "Only active candidates can manage saved searches");
        }

        return candidate;
    }

    // Ensure at least one search criterion exists
    private void validateSearchCriteria(
            String keyword,
            String location) {

        if (keyword == null && location == null) {
            throw new IllegalArgumentException(
                    "At least a keyword or location is required");
        }
    }

    // Trim input and convert blank values to null
    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    // Convert entity to response DTO
    private SavedSearchResponse mapToResponse(
            SavedSearch savedSearch) {

        SavedSearchResponse response = new SavedSearchResponse();

        response.setId(savedSearch.getId());
        response.setKeyword(savedSearch.getKeyword());
        response.setLocation(savedSearch.getLocation());
        response.setCreatedAt(savedSearch.getCreatedAt());
        response.setUpdatedAt(savedSearch.getUpdatedAt());

        return response;
    }
}
