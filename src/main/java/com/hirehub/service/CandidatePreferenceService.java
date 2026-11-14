
package com.hirehub.service;

import org.springframework.stereotype.Service;
import com.hirehub.exception.CandidatePreferenceNotFoundException;
import com.hirehub.exception.CandidatePreferenceAlreadyExistsException;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.dto.PreferenceRequest;
import com.hirehub.dto.PreferenceResponse;
import com.hirehub.entity.AccountStatus;
import com.hirehub.entity.CandidatePreference;
import com.hirehub.entity.User;
import com.hirehub.exception.ForbiddenAccessException;
import com.hirehub.exception.UserNotFoundException;
import com.hirehub.repository.CandidatePreferenceRepository;
import com.hirehub.repository.UserRepository;

@Service
public class CandidatePreferenceService {

    private final CandidatePreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public CandidatePreferenceService(
            CandidatePreferenceRepository preferenceRepository,
            UserRepository userRepository) {

        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PreferenceResponse createPreferences(
            Long candidateId, PreferenceRequest request) {

        User candidate = getCandidate(candidateId);

        if (preferenceRepository.existsByCandidateId(candidateId)) {
        	throw new CandidatePreferenceAlreadyExistsException(
        	        "Candidate preferences already exist");
        }

        CandidatePreference preference = new CandidatePreference();

        preference.setCandidate(candidate);
        preference.setSkills(request.getSkills());
        preference.setPreferredLocations(request.getPreferredLocations());
        preference.setExperienceLevel(request.getExperienceLevel());
        preference.setRemotePreference(request.getRemotePreference());

        CandidatePreference saved =
                preferenceRepository.save(preference);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public PreferenceResponse getPreferences(Long candidateId) {

        getCandidate(candidateId);

        CandidatePreference preference =
                preferenceRepository.findByCandidateId(candidateId)
                .orElseThrow(() -> new CandidatePreferenceNotFoundException(
                        "Candidate preferences not found"));

        return mapToResponse(preference);
    }

    @Transactional
    public PreferenceResponse updatePreferences(
            Long candidateId, PreferenceRequest request) {

        getCandidate(candidateId);

        CandidatePreference preference =
                preferenceRepository.findByCandidateId(candidateId)
                .orElseThrow(() -> new CandidatePreferenceNotFoundException(
                        "Candidate preferences not found"));

        preference.setSkills(request.getSkills());
        preference.setPreferredLocations(request.getPreferredLocations());
        preference.setExperienceLevel(request.getExperienceLevel());
        preference.setRemotePreference(request.getRemotePreference());

        CandidatePreference updated =
        		preferenceRepository.saveAndFlush(preference);

        return mapToResponse(updated);
    }

    private User getCandidate(Long candidateId) {

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Candidate not found"));

        if (!"CANDIDATE".equals(candidate.getRole())) {
            throw new ForbiddenAccessException(
                    "Only candidates can manage job preferences");
        }

        if (candidate.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ForbiddenAccessException(
                    "Inactive candidates cannot manage job preferences");
        }

        return candidate;
    }

    private PreferenceResponse mapToResponse(
            CandidatePreference preference) {

        PreferenceResponse response = new PreferenceResponse();

        response.setId(preference.getId());
        response.setSkills(preference.getSkills());
        response.setPreferredLocations(preference.getPreferredLocations());
        response.setExperienceLevel(preference.getExperienceLevel());
        response.setRemotePreference(preference.isRemotePreference());
        response.setCreatedAt(preference.getCreatedAt());
        response.setUpdatedAt(preference.getUpdatedAt());

        return response;
    }
}
