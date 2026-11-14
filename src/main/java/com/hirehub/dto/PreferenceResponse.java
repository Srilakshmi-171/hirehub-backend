
package com.hirehub.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class PreferenceResponse {

    private Long id;
    private Set<String> skills;
    private Set<String> preferredLocations;
    private String experienceLevel;
    private boolean remotePreference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public Set<String> getPreferredLocations() {
        return preferredLocations;
    }

    public void setPreferredLocations(Set<String> preferredLocations) {
        this.preferredLocations = preferredLocations;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public boolean isRemotePreference() {
        return remotePreference;
    }

    public void setRemotePreference(boolean remotePreference) {
        this.remotePreference = remotePreference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
