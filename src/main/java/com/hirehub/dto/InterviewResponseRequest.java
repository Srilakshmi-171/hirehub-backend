
package com.hirehub.dto;

import jakarta.validation.constraints.NotNull;

import com.hirehub.entity.InterviewStatus;

public class InterviewResponseRequest {

    @NotNull(message = "Interview response is required")
    private InterviewStatus response;

    public InterviewStatus getResponse() {
        return response;
    }

    public void setResponse(InterviewStatus response) {
        this.response = response;
    }
}
