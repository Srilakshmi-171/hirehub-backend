
package com.hirehub.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.dto.ApplicationAnalyticsResponse;
import com.hirehub.dto.HiringFunnelResponse;
import com.hirehub.dto.InterviewAnalyticsResponse;
import com.hirehub.dto.JobAnalyticsResponse;
import com.hirehub.dto.JobPerformanceResponse;
import com.hirehub.dto.RecruiterAnalyticsResponse;

import com.hirehub.entity.ApplicationStatus;
import com.hirehub.entity.InterviewResult;
import com.hirehub.entity.InterviewStatus;
import com.hirehub.entity.JobStatus;

import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.InterviewRepository;
import com.hirehub.repository.JobRepository;

@Service
@Transactional(readOnly = true)
public class RecruiterAnalyticsService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;

    public RecruiterAnalyticsService(
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            InterviewRepository interviewRepository) {

        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
    }

    public RecruiterAnalyticsResponse getAnalytics(
            Long recruiterId,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate != null && endDate != null
                && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }

        LocalDateTime startDateTime =
                startDate == null ? null : startDate.atStartOfDay();

        LocalDateTime endDateTime =
                endDate == null ? null : endDate.atTime(LocalTime.MAX);

        RecruiterAnalyticsResponse response =
                new RecruiterAnalyticsResponse();

        // Fetch application status counts only once
        Map<ApplicationStatus, Long> applicationCounts =
                getApplicationStatusCounts(
                        recruiterId, startDateTime, endDateTime);

        response.setStartDate(startDate);
        response.setEndDate(endDate);

        response.setJobOverview(
                buildJobAnalytics(
                        recruiterId, startDateTime, endDateTime));

        response.setApplicationOverview(
                buildApplicationAnalytics(applicationCounts));

        response.setInterviewOverview(
                buildInterviewAnalytics(
                        recruiterId, startDateTime, endDateTime));

        response.setHiringFunnel(
                buildHiringFunnel(applicationCounts));

        response.setTopJobs(
                buildTopJobs(
                        recruiterId, startDateTime, endDateTime));

        return response;
    }

    // Job analytics

    private JobAnalyticsResponse buildJobAnalytics(
            Long recruiterId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        JobAnalyticsResponse response = new JobAnalyticsResponse();

        response.setTotalJobs(
                jobRepository.countTotalJobsForAnalytics(
                        recruiterId, startDate, endDate));

        response.setOpenJobs(
                jobRepository.countActiveJobsByStatusForAnalytics(
                        recruiterId, JobStatus.OPEN,
                        startDate, endDate));

        response.setClosedJobs(
                jobRepository.countActiveJobsByStatusForAnalytics(
                        recruiterId, JobStatus.CLOSED,
                        startDate, endDate));

        response.setDeletedJobs(
                jobRepository.countDeletedJobsForAnalytics(
                        recruiterId, startDate, endDate));

        return response;
    }

    // Application analytics

    private ApplicationAnalyticsResponse buildApplicationAnalytics(
            Map<ApplicationStatus, Long> counts) {

        ApplicationAnalyticsResponse response =
                new ApplicationAnalyticsResponse();

        long applied = getApplicationCount(
                counts, ApplicationStatus.APPLIED);

        long shortlisted = getApplicationCount(
                counts, ApplicationStatus.SHORTLISTED);

        long interview = getApplicationCount(
                counts, ApplicationStatus.INTERVIEW);

        long selected = getApplicationCount(
                counts, ApplicationStatus.SELECTED);

        long rejected = getApplicationCount(
                counts, ApplicationStatus.REJECTED);

        long withdrawn = getApplicationCount(
                counts, ApplicationStatus.WITHDRAWN);

        response.setApplied(applied);
        response.setShortlisted(shortlisted);
        response.setInterview(interview);
        response.setSelected(selected);
        response.setRejected(rejected);
        response.setWithdrawn(withdrawn);

        response.setTotalApplications(
                applied + shortlisted + interview
                        + selected + rejected + withdrawn);

        return response;
    }

    // Hiring funnel

    private HiringFunnelResponse buildHiringFunnel(
            Map<ApplicationStatus, Long> counts) {

        HiringFunnelResponse response = new HiringFunnelResponse();

        response.setApplied(
                getApplicationCount(counts, ApplicationStatus.APPLIED));

        response.setShortlisted(
                getApplicationCount(counts, ApplicationStatus.SHORTLISTED));

        response.setInterview(
                getApplicationCount(counts, ApplicationStatus.INTERVIEW));

        response.setSelected(
                getApplicationCount(counts, ApplicationStatus.SELECTED));

        return response;
    }

    // Interview analytics

    private InterviewAnalyticsResponse buildInterviewAnalytics(
            Long recruiterId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        InterviewAnalyticsResponse response =
                new InterviewAnalyticsResponse();

        response.setTotalInterviews(
                interviewRepository.countTotalInterviewsForAnalytics(
                        recruiterId, startDate, endDate));

        List<Object[]> statusResults =
                interviewRepository.countInterviewsByStatusForAnalytics(
                        recruiterId, startDate, endDate);

        for (Object[] row : statusResults) {

            InterviewStatus status = (InterviewStatus) row[0];
            long count = ((Number) row[1]).longValue();

            switch (status) {
                case SCHEDULED ->
                    response.setScheduled(count);
                case ACCEPTED ->
                    response.setAccepted(count);
                case REJECTED ->
                    response.setRejected(count);
                case RESCHEDULE_REQUESTED ->
                    response.setRescheduleRequested(count);
                case CANCELLED ->
                    response.setCancelled(count);
                case COMPLETED ->
                    response.setCompleted(count);
            }
        }

        List<Object[]> resultRows =
                interviewRepository.countInterviewsByResultForAnalytics(
                        recruiterId, startDate, endDate);

        for (Object[] row : resultRows) {

            InterviewResult result = (InterviewResult) row[0];
            long count = ((Number) row[1]).longValue();

            switch (result) {
                case PASSED ->
                    response.setPassed(count);
                case FAILED ->
                    response.setFailed(count);
                case ON_HOLD ->
                    response.setOnHold(count);
            }
        }

        return response;
    }

    // Top-performing jobs

    private List<JobPerformanceResponse> buildTopJobs(
            Long recruiterId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        List<Object[]> results =
                applicationRepository.findTopJobsByApplicationsForAnalytics(
                        recruiterId,
                        startDate,
                        endDate,
                        PageRequest.of(0, 5));

        List<JobPerformanceResponse> topJobs = new ArrayList<>();

        for (Object[] row : results) {

            JobPerformanceResponse response =
                    new JobPerformanceResponse();

            response.setJobId(((Number) row[0]).longValue());
            response.setJobTitle((String) row[1]);
            response.setTotalApplications(
                    ((Number) row[2]).longValue());

            topJobs.add(response);
        }

        return topJobs;
    }

    // Shared application status query

    private Map<ApplicationStatus, Long> getApplicationStatusCounts(
            Long recruiterId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        List<Object[]> results =
                applicationRepository.countApplicationsByStatusForAnalytics(
                        recruiterId, startDate, endDate);

        Map<ApplicationStatus, Long> counts = new HashMap<>();

        for (Object[] row : results) {

            ApplicationStatus status = (ApplicationStatus) row[0];
            Long count = ((Number) row[1]).longValue();

            counts.put(status, count);
        }

        return counts;
    }

    // Helper method

    private long getApplicationCount(
            Map<ApplicationStatus, Long> counts,
            ApplicationStatus status) {

        return counts.getOrDefault(status, 0L);
    }
}
