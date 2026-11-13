package com.hirehub.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RescheduleInterviewRequest {

    @NotNull(message = "Proposed interview time is required")
    @Future(message = "Proposed interview time must be in the future")
    private LocalDateTime requestedNewTime;

    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String rescheduleReason;

    public LocalDateTime getRequestedNewTime() {
        return requestedNewTime;
    }

    public void setRequestedNewTime(LocalDateTime requestedNewTime) {
        this.requestedNewTime = requestedNewTime;
    }

    public String getRescheduleReason() {
        return rescheduleReason;
    }

    public void setRescheduleReason(String rescheduleReason) {
        this.rescheduleReason = rescheduleReason;
    }
}