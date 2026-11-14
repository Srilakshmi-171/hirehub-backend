
package com.hirehub.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "candidate_preferences",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_candidate_preference",
            columnNames = "candidate_id"
        )
    }
)
public class CandidatePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false, unique = true)
    private User candidate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "candidate_preference_skills",
        joinColumns = @JoinColumn(name = "preference_id")
    )
    @Column(name = "skill", nullable = false)
    private Set<String> skills = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "candidate_preferred_locations",
        joinColumns = @JoinColumn(name = "preference_id")
    )
    @Column(name = "location", nullable = false)
    private Set<String> preferredLocations = new HashSet<>();

    @Column(nullable = false)
    private String experienceLevel;

    @Column(nullable = false)
    private boolean remotePreference = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public CandidatePreference() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getCandidate() {
		return candidate;
	}

	public void setCandidate(User candidate) {
		this.candidate = candidate;
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

    // Generate getters and setters for all fields.
}
