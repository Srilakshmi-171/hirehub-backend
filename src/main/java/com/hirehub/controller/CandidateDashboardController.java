package com.hirehub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.dto.CandidateDashboardResponse;
import com.hirehub.service.CandidateDashboardService;

@RestController
@RequestMapping("/api/candidate")
public class CandidateDashboardController {

    private final CandidateDashboardService candidateDashboardService;

    public CandidateDashboardController(
            CandidateDashboardService candidateDashboardService) {

        this.candidateDashboardService = candidateDashboardService;
    }

    @GetMapping("/dashboard")
    public CandidateDashboardResponse getDashboard(
            Authentication authentication) {

        Long candidateId =
                (Long) authentication.getPrincipal();

        return candidateDashboardService.getDashboard(candidateId);
    }
}