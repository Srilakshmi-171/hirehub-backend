package com.hirehub.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hirehub.dto.CandidateDashboardResponse;
import com.hirehub.dto.RecentApplicationResponse;
import com.hirehub.entity.Application;
import com.hirehub.entity.ApplicationStatus;
import com.hirehub.exception.ForbiddenAccessException;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.UserRepository;

@Service
public class CandidateDashboardService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public CandidateDashboardService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public CandidateDashboardResponse getDashboard(Long candidateId) {

        // Verify that the user exists and has the CANDIDATE role
        var candidate = userRepository.findById(candidateId)
                .orElseThrow(() ->
                        new ForbiddenAccessException(
                                "Candidate account not found"));

        if (!"CANDIDATE".equals(candidate.getRole())) {
            throw new ForbiddenAccessException(
                    "Only candidates can access the candidate dashboard");
        }

        // Total applications
        long totalApplications =
                applicationRepository.countByCandidateId(candidateId);

        // Initialize all statuses with zero
        Map<ApplicationStatus, Long> statusCounts =
                new EnumMap<>(ApplicationStatus.class);

        for (ApplicationStatus status : ApplicationStatus.values()) {
            statusCounts.put(status, 0L);
        }

        // Populate actual status counts from the database
        List<Object[]> statusResults =
                applicationRepository
                        .countApplicationsByStatusForCandidate(candidateId);

        for (Object[] result : statusResults) {
            ApplicationStatus status =
                    (ApplicationStatus) result[0];

            Long count = (Long) result[1];

            statusCounts.put(status, count);
        }

        // Fetch the five most recent applications
        List<Application> recentApplications =
                applicationRepository
                        .findTop5ByCandidateIdOrderByAppliedAtDesc(candidateId);

        List<RecentApplicationResponse> recentResponses =
                new ArrayList<>();

        for (Application application : recentApplications) {

            RecentApplicationResponse response =
                    new RecentApplicationResponse();

            response.setApplicationId(application.getId());

            response.setJobId(application.getJob().getId());

            response.setJobTitle(application.getJob().getTitle());

            response.setCompanyName(
                    application.getJob().getCompanyName());

            response.setLocation(
                    application.getJob().getLocation());

            response.setStatus(application.getStatus());

            response.setAppliedAt(application.getAppliedAt());

            recentResponses.add(response);
        }

        // Build dashboard response
        CandidateDashboardResponse dashboard =
                new CandidateDashboardResponse();

        dashboard.setTotalApplications(totalApplications);
        dashboard.setStatusCounts(statusCounts);
        dashboard.setRecentApplications(recentResponses);

        return dashboard;
    }
}