
package com.hirehub.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hirehub.dto.ApplicationResponse;
import com.hirehub.entity.Application;
import com.hirehub.entity.ApplicationStatus;
import com.hirehub.entity.ApplicationStatusHistory;
import com.hirehub.entity.Job;
import com.hirehub.entity.JobStatus;
import com.hirehub.entity.User;
import com.hirehub.exception.ApplicationNotFoundException;
import com.hirehub.exception.DuplicateApplicationException;
import com.hirehub.exception.JobNotFoundException;
import com.hirehub.exception.JobNotOpenException;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.ApplicationStatusHistoryRepository;
import com.hirehub.repository.JobRepository;
import com.hirehub.repository.UserRepository;
import com.hirehub.util.EmailTemplateUtil;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final JobService jobService;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ApplicationStatusHistoryRepository historyRepository,
            UserRepository userRepository,
            JobService jobService,
            JobRepository jobRepository,
            NotificationService notificationService,
            EmailService emailService) {

        this.applicationRepository = applicationRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.jobService = jobService;
        this.jobRepository = jobRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    // Record an application status history event
    private void recordStatusHistory(
            Application application,
            ApplicationStatus previousStatus,
            ApplicationStatus newStatus,
            User changedBy,
            String remarks) {

        ApplicationStatusHistory history =
                new ApplicationStatusHistory();

        history.setApplication(application);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        history.setRemarks(remarks);

        historyRepository.save(history);
    }

    // Apply for a job
    @Transactional
    public Application applyForJob(Long jobId, Long candidateId) {

        Job job = jobService.getJobById(jobId);

        if (job.getStatus() != JobStatus.OPEN) {
            throw new JobNotOpenException(
                    "Applications are not allowed for this job");
        }

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Candidate not found"));

        if (applicationRepository.existsByCandidateIdAndJobId(
                candidateId, jobId)) {
            throw new DuplicateApplicationException(
                    "You have already applied for this job");
        }

        Application application = new Application();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);

        Application savedApplication =
                applicationRepository.save(application);

        // Record initial application history
        recordStatusHistory(
                savedApplication,
                null,
                ApplicationStatus.APPLIED,
                candidate,
                "Candidate applied for the job");

        notificationService.createNotification(
                job.getRecruiter(),
                candidate.getName() + " applied for your job: "
                        + job.getTitle(),
                "APPLICATION");

        // Send application confirmation email
        String htmlBody = EmailTemplateUtil.applicationConfirmation(
                candidate.getName(),
                job.getTitle(),
                job.getCompanyName()
        );

        emailService.sendHtmlEmail(
                candidate.getEmail(),
                "Application Confirmation - " + job.getTitle(),
                htmlBody
        );

        return savedApplication;
    }

    // Validate allowed application status transitions
    private boolean isAllowedStatusTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus) {

        if (currentStatus == null || newStatus == null) {
            return false;
        }

        return switch (currentStatus) {

            case APPLIED ->
                    newStatus == ApplicationStatus.SHORTLISTED
                    || newStatus == ApplicationStatus.INTERVIEW
                    || newStatus == ApplicationStatus.REJECTED;

            case SHORTLISTED ->
                    newStatus == ApplicationStatus.INTERVIEW
                    || newStatus == ApplicationStatus.REJECTED;

            case INTERVIEW ->
                    newStatus == ApplicationStatus.SELECTED
                    || newStatus == ApplicationStatus.REJECTED;

            case SELECTED, REJECTED, WITHDRAWN -> false;
        };
    }

    // Recruiter updates application status
    @Transactional
    public Application updateApplicationStatus(
            Long applicationId,
            Long recruiterId,
            ApplicationStatus newStatus) {

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found with ID: " + applicationId));

        Long ownerId = application.getJob()
                .getRecruiter()
                .getId();

        if (!ownerId.equals(recruiterId)) {
            throw new AccessDeniedException(
                    "You do not own this job");
        }

        if (newStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Application status is required");
        }

        ApplicationStatus oldStatus = application.getStatus();

        // Avoid duplicate updates and notifications
        if (oldStatus == newStatus) {
            return application;
        }

        // Validate the requested transition
        if (!isAllowedStatusTransition(oldStatus, newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid application status transition: "
                            + oldStatus + " -> " + newStatus);
        }

        application.setStatus(newStatus);

        Application updatedApplication =
                applicationRepository.save(application);

        // Record status change history
        recordStatusHistory(
                updatedApplication,
                oldStatus,
                newStatus,
                application.getJob().getRecruiter(),
                "Recruiter updated application status");

        notificationService.createNotification(
                application.getCandidate(),
                "Your application for "
                        + application.getJob().getTitle()
                        + " has been updated to "
                        + newStatus,
                "STATUS_UPDATE");

        // Send application status update email
        String htmlBody = EmailTemplateUtil.applicationStatusUpdate(
                application.getCandidate().getName(),
                application.getJob().getTitle(),
                newStatus.name()
        );

        emailService.sendHtmlEmail(
                application.getCandidate().getEmail(),
                "Application Status Update - "
                        + application.getJob().getTitle(),
                htmlBody
        );

        return updatedApplication;
    }

    // Candidate views their applications
    public List<ApplicationResponse> getMyApplications(Long candidateId) {

        List<Application> applications =
                applicationRepository.findByCandidateId(candidateId);

        return applications.stream()
                .map(application -> {

                    ApplicationResponse response =
                            new ApplicationResponse();

                    response.setApplicationId(application.getId());
                    response.setJobId(application.getJob().getId());
                    response.setJobTitle(application.getJob().getTitle());
                    response.setCompanyName(
                            application.getJob().getCompanyName());
                    response.setLocation(
                            application.getJob().getLocation());
                    response.setStatus(application.getStatus());
                    response.setAppliedAt(application.getAppliedAt());

                    return response;
                })
                .toList();
    }

    // Candidate withdraws an application
    @Transactional
    public Application withdrawApplication(
            Long applicationId,
            Long candidateId) {

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found with ID: " + applicationId));

        Long ownerId = application.getCandidate().getId();

        if (!ownerId.equals(candidateId)) {
            throw new AccessDeniedException(
                    "You can withdraw only your own application");
        }

        ApplicationStatus currentStatus = application.getStatus();

        if (currentStatus == ApplicationStatus.SELECTED
                || currentStatus == ApplicationStatus.REJECTED
                || currentStatus == ApplicationStatus.WITHDRAWN) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Application cannot be withdrawn in its current status");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);

        Application withdrawnApplication =
                applicationRepository.save(application);

        // Record withdrawal history
        recordStatusHistory(
                withdrawnApplication,
                currentStatus,
                ApplicationStatus.WITHDRAWN,
                application.getCandidate(),
                "Candidate withdrew the application");

        notificationService.createNotification(
                application.getJob().getRecruiter(),
                application.getCandidate().getName()
                        + " withdrew their application for "
                        + application.getJob().getTitle(),
                "WITHDRAWAL");

        return withdrawnApplication;
    }

    // Recruiter views applicants for their job
    public List<ApplicationResponse> getApplicantsForJob(
            Long jobId, Long recruiterId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found"));

        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new AccessDeniedException(
                    "You are not authorized to view applicants for this job");
        }

        List<Application> applications =
                applicationRepository.findByJobId(jobId);

        return applications.stream()
                .map(application -> {

                    ApplicationResponse response =
                            new ApplicationResponse();

                    response.setApplicationId(application.getId());
                    response.setJobId(application.getJob().getId());
                    response.setJobTitle(application.getJob().getTitle());
                    response.setCompanyName(
                            application.getJob().getCompanyName());
                    response.setLocation(
                            application.getJob().getLocation());
                    response.setStatus(application.getStatus());
                    response.setAppliedAt(application.getAppliedAt());

                    return response;
                })
                .toList();
    }

    // Recruiter dashboard: application counts by status
    public Map<ApplicationStatus, Long> getApplicationCountsByStatus(
            Long recruiterId) {

        Map<ApplicationStatus, Long> counts =
                new EnumMap<>(ApplicationStatus.class);

        for (ApplicationStatus status : ApplicationStatus.values()) {
            counts.put(status, 0L);
        }

        List<Object[]> results =
                applicationRepository
                        .countApplicationsByStatusForRecruiter(recruiterId);

        for (Object[] result : results) {

            ApplicationStatus status =
                    (ApplicationStatus) result[0];

            Long count = (Long) result[1];

            counts.put(status, count);
        }

        return counts;
    }
    

    // Automatically move an application to INTERVIEW when an interview is scheduled
    @Transactional
    public Application markApplicationAsInterview(
            Long applicationId,
            Long recruiterId) {

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found with ID: " + applicationId));

        Long ownerId = application.getJob()
                .getRecruiter()
                .getId();

        if (!ownerId.equals(recruiterId)) {
            throw new AccessDeniedException(
                    "You do not own this job");
        }

        ApplicationStatus oldStatus = application.getStatus();

        // Already in the interview stage: no duplicate history or notifications
        if (oldStatus == ApplicationStatus.INTERVIEW) {
            return application;
        }

        // Only APPLIED and SHORTLISTED applications can enter INTERVIEW
        if (!isAllowedStatusTransition(
                oldStatus, ApplicationStatus.INTERVIEW)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Application cannot move to INTERVIEW from "
                            + oldStatus);
        }

        application.setStatus(ApplicationStatus.INTERVIEW);

        Application updatedApplication =
                applicationRepository.save(application);

        // Record status history
        recordStatusHistory(
                updatedApplication,
                oldStatus,
                ApplicationStatus.INTERVIEW,
                application.getJob().getRecruiter(),
                "Application moved to INTERVIEW because an interview was scheduled");

        // Notify candidate
        notificationService.createNotification(
                application.getCandidate(),
                "Your application for "
                        + application.getJob().getTitle()
                        + " has moved to INTERVIEW",
                "STATUS_UPDATE");

        // Send status update email
        String htmlBody = EmailTemplateUtil.applicationStatusUpdate(
                application.getCandidate().getName(),
                application.getJob().getTitle(),
                ApplicationStatus.INTERVIEW.name()
        );

        emailService.sendHtmlEmail(
                application.getCandidate().getEmail(),
                "Application Status Update - "
                        + application.getJob().getTitle(),
                htmlBody
        );

        return updatedApplication;
    }

}
