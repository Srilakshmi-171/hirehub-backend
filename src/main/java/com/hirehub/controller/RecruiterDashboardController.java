package com.hirehub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.dto.RecruiterDashboardResponse;
import com.hirehub.service.RecruiterDashboardService;

@RestController
@RequestMapping("/api/recruiter/dashboard")
public class RecruiterDashboardController {

    private final RecruiterDashboardService dashboardService;

    public RecruiterDashboardController(
            RecruiterDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<RecruiterDashboardResponse> getDashboard(
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        RecruiterDashboardResponse response =
                dashboardService.getDashboard(recruiterId);

        return ResponseEntity.ok(response);
    }
}