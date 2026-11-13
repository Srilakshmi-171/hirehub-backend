package com.hirehub.dto;

import com.hirehub.entity.InterviewResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CompleteInterviewRequest {

    @NotBlank(message = "Recruiter feedback is required")
    @Size(max = 3000, message = "Feedback cannot exceed 3000 characters")
    private String recruiterFeedback;

    @NotNull(message = "Interview result is required")
    private InterviewResult result;

    public String getRecruiterFeedback() {
        return recruiterFeedback;
    }

    public void setRecruiterFeedback(String recruiterFeedback) {
        this.recruiterFeedback = recruiterFeedback;
    }

    public InterviewResult getResult() {
        return result;
    }

    public void setResult(InterviewResult result) {
        this.result = result;
    }
}