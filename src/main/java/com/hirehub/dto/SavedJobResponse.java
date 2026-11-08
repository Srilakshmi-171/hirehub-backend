package com.hirehub.dto;

import java.time.LocalDateTime;

public class SavedJobResponse {

    private Long savedJobId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private LocalDateTime savedAt;
	public Long getSavedJobId() {
		return savedJobId;
	}
	public void setSavedJobId(Long savedJobId) {
		this.savedJobId = savedJobId;
	}
	public Long getJobId() {
		return jobId;
	}
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public LocalDateTime getSavedAt() {
		return savedAt;
	}
	public void setSavedAt(LocalDateTime savedAt) {
		this.savedAt = savedAt;
	}

    // Getters and Setters
}