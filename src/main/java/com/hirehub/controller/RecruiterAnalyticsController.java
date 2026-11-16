
package com.hirehub.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.dto.RecruiterAnalyticsResponse;
import com.hirehub.service.RecruiterAnalyticsService;

@RestController
@RequestMapping("/api/recruiter/analytics")
public class RecruiterAnalyticsController {

    private final RecruiterAnalyticsService analyticsService;

    public RecruiterAnalyticsController(
            RecruiterAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<RecruiterAnalyticsResponse> getAnalytics(
            Authentication authentication,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        Long recruiterId = (Long) authentication.getPrincipal();

        RecruiterAnalyticsResponse response =
                analyticsService.getAnalytics(
                        recruiterId, startDate, endDate);

        return ResponseEntity.ok(response);
    }
}
