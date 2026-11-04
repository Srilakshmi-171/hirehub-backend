package com.hirehub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hirehub.dto.RecruiterProfileRequest;
import com.hirehub.dto.RecruiterProfileResponse;
import com.hirehub.service.RecruiterProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recruiters/me/profile")
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;

    public RecruiterProfileController(
            RecruiterProfileService recruiterProfileService) {
        this.recruiterProfileService = recruiterProfileService;
    }

    @GetMapping
    public RecruiterProfileResponse getMyProfile(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return recruiterProfileService.getMyProfile(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecruiterProfileResponse createMyProfile(
            Authentication authentication,
            @Valid @RequestBody RecruiterProfileRequest request) {

        Long userId = (Long) authentication.getPrincipal();

        return recruiterProfileService.createMyProfile(userId, request);
    }

    @PatchMapping
    public RecruiterProfileResponse updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody RecruiterProfileRequest request) {

        Long userId = (Long) authentication.getPrincipal();

        return recruiterProfileService.updateMyProfile(userId, request);
    }
}