package com.hirehub.dto;

import jakarta.validation.constraints.NotNull;

public class RescheduleDecisionRequest {

    @NotNull(message = "Approval decision is required")
    private Boolean approved;

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}