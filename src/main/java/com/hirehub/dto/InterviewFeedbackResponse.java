package com.hirehub.dto;

import com.hirehub.entity.InterviewResult;
import com.hirehub.entity.InterviewStatus;

public class InterviewFeedbackResponse {

    private Long interviewId;
    private Long applicationId;
    private String interviewTitle;
    private String recruiterFeedback;
    private InterviewResult result;
    private InterviewStatus status;

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getInterviewTitle() {
        return interviewTitle;
    }

    public void setInterviewTitle(String interviewTitle) {
        this.interviewTitle = interviewTitle;
    }

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

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }
}