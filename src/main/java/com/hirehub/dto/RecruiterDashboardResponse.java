package com.hirehub.dto;

import java.util.Map;

import com.hirehub.entity.ApplicationStatus;

public class RecruiterDashboardResponse {

    private long totalJobs;
    private long openJobs;
    private long totalApplications;
    private Map<ApplicationStatus, Long> applicationsByStatus;

    public long getTotalJobs() {
        return totalJobs;
    }

    public void setTotalJobs(long totalJobs) {
        this.totalJobs = totalJobs;
    }

    public long getOpenJobs() {
        return openJobs;
    }

    public void setOpenJobs(long openJobs) {
        this.openJobs = openJobs;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public Map<ApplicationStatus, Long> getApplicationsByStatus() {
        return applicationsByStatus;
    }

    public void setApplicationsByStatus(
            Map<ApplicationStatus, Long> applicationsByStatus) {
        this.applicationsByStatus = applicationsByStatus;
    }
}