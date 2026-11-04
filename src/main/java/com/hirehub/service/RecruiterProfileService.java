
package com.hirehub.service;

import org.springframework.stereotype.Service;

import com.hirehub.dto.RecruiterProfileRequest;
import com.hirehub.dto.RecruiterProfileResponse;
import com.hirehub.entity.RecruiterProfile;
import com.hirehub.entity.User;
import com.hirehub.exception.UserNotFoundException;
import com.hirehub.exception.RecruiterProfileNotFoundException;
import com.hirehub.exception.ForbiddenAccessException;
import com.hirehub.exception.RecruiterProfileAlreadyExistsException;
import com.hirehub.repository.RecruiterProfileRepository;
import com.hirehub.repository.UserRepository;

@Service
public class RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;

    public RecruiterProfileService(
            RecruiterProfileRepository recruiterProfileRepository,
            UserRepository userRepository) {

        this.recruiterProfileRepository = recruiterProfileRepository;
        this.userRepository = userRepository;
    }

    public RecruiterProfileResponse getMyProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!"RECRUITER".equals(user.getRole())) {
        	throw new ForbiddenAccessException(
        	        "Only recruiters can access recruiter profiles");
        }

        RecruiterProfile profile = recruiterProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RecruiterProfileNotFoundException(
                                "Recruiter profile not found"));

        return mapToResponse(profile);
    }

    public RecruiterProfileResponse createMyProfile(
            Long userId,
            RecruiterProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!"RECRUITER".equals(user.getRole())) {
            throw new ForbiddenAccessException(
                    "Only recruiters can create recruiter profiles");
        }

        if (recruiterProfileRepository.existsByUserId(userId)) {
            throw new RecruiterProfileAlreadyExistsException(
                    "Recruiter profile already exists");
        }

        RecruiterProfile profile = new RecruiterProfile();

        profile.setUser(user);
        profile.setCompanyName(request.getCompanyName());
        profile.setDesignation(request.getDesignation());
        profile.setCompanyWebsite(request.getCompanyWebsite());
        profile.setCompanyDescription(request.getCompanyDescription());
        profile.setCompanyLocation(request.getCompanyLocation());
        profile.setContactNumber(request.getContactNumber());

        RecruiterProfile savedProfile =
                recruiterProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    public RecruiterProfileResponse updateMyProfile(
            Long userId,
            RecruiterProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!"RECRUITER".equals(user.getRole())) {
        	throw new ForbiddenAccessException(
        	        "Only recruiters can access recruiter profiles");
        }

        RecruiterProfile profile = recruiterProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RecruiterProfileNotFoundException(
                                "Recruiter profile not found"));

        profile.setCompanyName(request.getCompanyName());
        profile.setDesignation(request.getDesignation());
        profile.setCompanyWebsite(request.getCompanyWebsite());
        profile.setCompanyDescription(request.getCompanyDescription());
        profile.setCompanyLocation(request.getCompanyLocation());
        profile.setContactNumber(request.getContactNumber());

        RecruiterProfile updatedProfile =
                recruiterProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    private RecruiterProfileResponse mapToResponse(
            RecruiterProfile profile) {

        RecruiterProfileResponse response =
                new RecruiterProfileResponse();

        response.setProfileId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setName(profile.getUser().getName());
        response.setEmail(profile.getUser().getEmail());

        response.setCompanyName(profile.getCompanyName());
        response.setDesignation(profile.getDesignation());
        response.setCompanyWebsite(profile.getCompanyWebsite());
        response.setCompanyDescription(profile.getCompanyDescription());
        response.setCompanyLocation(profile.getCompanyLocation());
        response.setContactNumber(profile.getContactNumber());

        return response;
    }
}
