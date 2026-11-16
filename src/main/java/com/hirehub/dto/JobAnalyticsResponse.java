
package com.hirehub.dto;

public class JobAnalyticsResponse {

    private long totalJobs;
    private long openJobs;
    private long closedJobs;
    private long deletedJobs;

    public JobAnalyticsResponse() {
    }

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

    public long getClosedJobs() {
        return closedJobs;
    }

    public void setClosedJobs(long closedJobs) {
        this.closedJobs = closedJobs;
    }

    public long getDeletedJobs() {
        return deletedJobs;
    }

    public void setDeletedJobs(long deletedJobs) {
        this.deletedJobs = deletedJobs;
    }
}
