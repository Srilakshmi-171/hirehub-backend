package com.hirehub.dto;

import java.util.List;
import java.util.Map;

import com.hirehub.entity.ApplicationStatus;

public class CandidateDashboardResponse {

    private long totalApplications;
    private Map<ApplicationStatus, Long> statusCounts;
    private List<RecentApplicationResponse> recentApplications;

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public Map<ApplicationStatus, Long> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(
            Map<ApplicationStatus, Long> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public List<RecentApplicationResponse> getRecentApplications() {
        return recentApplications;
    }

    public void setRecentApplications(
            List<RecentApplicationResponse> recentApplications) {
        this.recentApplications = recentApplications;
    }
}