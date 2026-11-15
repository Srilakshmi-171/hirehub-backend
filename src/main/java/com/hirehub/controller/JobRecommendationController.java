
package com.hirehub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.dto.JobRecommendationResponse;
import com.hirehub.service.JobRecommendationService;

@RestController
@RequestMapping("/api/candidates/me/recommendations")
public class JobRecommendationController {

    private final JobRecommendationService recommendationService;

    public JobRecommendationController(
            JobRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<JobRecommendationResponse>>
            getRecommendations(Authentication authentication) {

        Long candidateId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                recommendationService.getRecommendations(candidateId));
    }
}
