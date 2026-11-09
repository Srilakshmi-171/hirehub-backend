
package com.hirehub.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "application_status_history")
public class ApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Application associated with this status change
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // Previous status (null for initial application)
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private ApplicationStatus previousStatus;

    // New application status
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ApplicationStatus newStatus;

    // User who performed the action
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    // Date and time of the status change
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    // Optional remarks
    @Column(length = 500)
    private String remarks;

    @PrePersist
    protected void onCreate() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ApplicationStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ApplicationStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ApplicationStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ApplicationStatus newStatus) {
        this.newStatus = newStatus;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
