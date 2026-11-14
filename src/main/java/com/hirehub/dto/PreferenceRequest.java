
package com.hirehub.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PreferenceRequest {

    @NotEmpty(message = "At least one skill is required")
    @Size(max = 20, message = "Maximum 20 skills are allowed")
    private Set<
        @NotBlank(message = "Skill cannot be blank")
        @Size(max = 50, message = "Skill cannot exceed 50 characters")
        String
    > skills;

    @NotEmpty(message = "At least one preferred location is required")
    @Size(max = 10, message = "Maximum 10 locations are allowed")
    private Set<
        @NotBlank(message = "Location cannot be blank")
        @Size(max = 50, message = "Location cannot exceed 50 characters")
        String
    > preferredLocations;

    @NotBlank(message = "Experience level is required")
    @Size(max = 30, message = "Experience level cannot exceed 30 characters")
    private String experienceLevel;

    @NotNull(message = "Remote preference is required")
    private Boolean remotePreference;

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

    public Boolean getRemotePreference() {
        return remotePreference;
    }

    public void setRemotePreference(Boolean remotePreference) {
        this.remotePreference = remotePreference;
    }
}
