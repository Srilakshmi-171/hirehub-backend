
package com.hirehub.dto;

import jakarta.validation.constraints.NotNull;

import com.hirehub.entity.InterviewStatus;
import jakarta.validation.constraints.AssertTrue;

public class InterviewResponseRequest {

    @NotNull(message = "Interview response is required")
    private InterviewStatus response;

    @AssertTrue(message = "Response must be ACCEPTED or REJECTED")
    public boolean isResponseValid() {
        return response == null
                || response == InterviewStatus.ACCEPTED
                || response == InterviewStatus.REJECTED;
    }

    public InterviewStatus getResponse() {
        return response;
    }

    public void setResponse(InterviewStatus response) {
        this.response = response;
    }
}
