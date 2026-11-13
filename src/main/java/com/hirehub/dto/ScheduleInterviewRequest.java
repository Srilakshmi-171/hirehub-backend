
package com.hirehub.dto;

import java.time.LocalDateTime;

import com.hirehub.entity.InterviewMode;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ScheduleInterviewRequest {

    @NotNull(message = "Round number is required")
    @Min(value = 1, message = "Round number must be at least 1")
    private Integer roundNumber;

    @NotBlank(message = "Interview title is required")
    @Size(max = 150, message = "Interview title cannot exceed 150 characters")
    private String interviewTitle;

    @NotNull(message = "Interview date and time are required")
    @Future(message = "Interview date and time must be in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Interview mode is required")
    private InterviewMode mode;

    @Size(max = 500, message = "Meeting link cannot exceed 500 characters")
    private String meetingLink;

    @Size(max = 200, message = "Venue name cannot exceed 200 characters")
    private String venueName;

    @Size(max = 500, message = "Venue address cannot exceed 500 characters")
    private String venueAddress;

    @Size(max = 2000, message = "Instructions cannot exceed 2000 characters")
    private String instructions;

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getInterviewTitle() {
        return interviewTitle;
    }

    public void setInterviewTitle(String interviewTitle) {
        this.interviewTitle = interviewTitle;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public InterviewMode getMode() {
        return mode;
    }

    public void setMode(InterviewMode mode) {
        this.mode = mode;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
