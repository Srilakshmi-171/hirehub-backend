package com.hirehub.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.hirehub.dto.RecruiterDashboardResponse;
import com.hirehub.entity.ApplicationStatus;
import com.hirehub.entity.JobStatus;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.JobRepository;

@Service
public class RecruiterDashboardService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    private final ApplicationService applicationService;

    public RecruiterDashboardService(
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            ApplicationService applicationService) {

        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.applicationService = applicationService;
    }

    public RecruiterDashboardResponse getDashboard(Long recruiterId) {

        RecruiterDashboardResponse response =
                new RecruiterDashboardResponse();

        long totalJobs =
                jobRepository.countByRecruiterId(recruiterId);

        long openJobs =
                jobRepository.countByRecruiterIdAndStatus(
                        recruiterId, JobStatus.OPEN);

        Map<ApplicationStatus, Long> applicationCounts =
                applicationService
                        .getApplicationCountsByStatus(recruiterId);

        long totalApplications =
                applicationCounts.values()
                        .stream()
                        .mapToLong(Long::longValue)
                        .sum();

        response.setTotalJobs(totalJobs);
        response.setOpenJobs(openJobs);
        response.setTotalApplications(totalApplications);
        response.setApplicationsByStatus(applicationCounts);

        return response;
    }
}