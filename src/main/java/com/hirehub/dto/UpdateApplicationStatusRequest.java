package com.hirehub.dto;

import com.hirehub.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateApplicationStatusRequest {

    @NotNull(message = "Application status is required")
    private ApplicationStatus status;

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}