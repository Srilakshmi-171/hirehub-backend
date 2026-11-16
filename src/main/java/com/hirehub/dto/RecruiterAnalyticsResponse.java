
package com.hirehub.dto;

import java.time.LocalDate;
import java.util.List;

public class RecruiterAnalyticsResponse {

    private LocalDate startDate;
    private LocalDate endDate;

    private JobAnalyticsResponse jobOverview;

    private ApplicationAnalyticsResponse applicationOverview;

    private InterviewAnalyticsResponse interviewOverview;

    private HiringFunnelResponse hiringFunnel;

    private List<JobPerformanceResponse> topJobs;

    public RecruiterAnalyticsResponse() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public JobAnalyticsResponse getJobOverview() {
        return jobOverview;
    }

    public void setJobOverview(JobAnalyticsResponse jobOverview) {
        this.jobOverview = jobOverview;
    }

    public ApplicationAnalyticsResponse getApplicationOverview() {
        return applicationOverview;
    }

    public void setApplicationOverview(
            ApplicationAnalyticsResponse applicationOverview) {
        this.applicationOverview = applicationOverview;
    }

    public InterviewAnalyticsResponse getInterviewOverview() {
        return interviewOverview;
    }

    public void setInterviewOverview(
            InterviewAnalyticsResponse interviewOverview) {
        this.interviewOverview = interviewOverview;
    }

    public HiringFunnelResponse getHiringFunnel() {
        return hiringFunnel;
    }

    public void setHiringFunnel(HiringFunnelResponse hiringFunnel) {
        this.hiringFunnel = hiringFunnel;
    }

    public List<JobPerformanceResponse> getTopJobs() {
        return topJobs;
    }

    public void setTopJobs(List<JobPerformanceResponse> topJobs) {
        this.topJobs = topJobs;
    }
}
