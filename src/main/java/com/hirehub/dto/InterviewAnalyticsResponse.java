
package com.hirehub.dto;

public class InterviewAnalyticsResponse {

    private long totalInterviews;

    // Interview status counts
    private long scheduled;
    private long accepted;
    private long rejected;
    private long rescheduleRequested;
    private long cancelled;
    private long completed;

    // Interview result counts
    private long passed;
    private long failed;
    private long onHold;

    public InterviewAnalyticsResponse() {
    }

    public long getTotalInterviews() {
        return totalInterviews;
    }

    public void setTotalInterviews(long totalInterviews) {
        this.totalInterviews = totalInterviews;
    }

    public long getScheduled() {
        return scheduled;
    }

    public void setScheduled(long scheduled) {
        this.scheduled = scheduled;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getRescheduleRequested() {
        return rescheduleRequested;
    }

    public void setRescheduleRequested(long rescheduleRequested) {
        this.rescheduleRequested = rescheduleRequested;
    }

    public long getCancelled() {
        return cancelled;
    }

    public void setCancelled(long cancelled) {
        this.cancelled = cancelled;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getPassed() {
        return passed;
    }

    public void setPassed(long passed) {
        this.passed = passed;
    }

    public long getFailed() {
        return failed;
    }

    public void setFailed(long failed) {
        this.failed = failed;
    }

    public long getOnHold() {
        return onHold;
    }

    public void setOnHold(long onHold) {
        this.onHold = onHold;
    }
}
