package com.hirehub.service;

import com.hirehub.dto.ApplicationResponse;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.entity.Application;
import com.hirehub.entity.ApplicationStatus;
import com.hirehub.entity.Job;
import com.hirehub.entity.JobStatus;
import com.hirehub.entity.User;
import com.hirehub.exception.ApplicationNotFoundException;
import com.hirehub.exception.DuplicateApplicationException;
import com.hirehub.exception.JobNotFoundException;
import com.hirehub.exception.JobNotOpenException;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.JobRepository;
import com.hirehub.repository.UserRepository;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobService jobService;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobService jobService,
            JobRepository jobRepository,
            NotificationService notificationService) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobService = jobService;
        this.jobRepository = jobRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Application applyForJob(Long jobId, Long candidateId) {

        // 1. Find the job; this also rejects missing or deleted jobs.
        Job job = jobService.getJobById(jobId);

        // 2. A candidate can apply only to an open job.
        if (job.getStatus() != JobStatus.OPEN) {
            throw new JobNotOpenException(
                    "Applications are not allowed for this job");
        }

        // 3. Find the candidate.
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Candidate not found"));

        // 4. Prevent duplicate applications.
        if (applicationRepository.existsByCandidateIdAndJobId(
                candidateId, jobId)) {
            throw new DuplicateApplicationException(
                    "You have already applied for this job");
        }

        // 5. Create and save the application.
        Application application = new Application();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);

        Application savedApplication =
                applicationRepository.save(application);

        notificationService.createNotification(
                job.getRecruiter(),
                candidate.getName() + " applied for your job: "
                        + job.getTitle(),
                "APPLICATION");

        return savedApplication;
    }
    

@Transactional
public Application updateApplicationStatus(
        Long applicationId,
        Long recruiterId,
        ApplicationStatus newStatus) {

    // 1. Find the application.
    Application application = applicationRepository
            .findById(applicationId)
            .orElseThrow(() -> new ApplicationNotFoundException(
                    "Application not found with ID: " + applicationId));

    // 2. Verify that the recruiter owns the job.
    Long ownerId = application.getJob()
            .getRecruiter()
            .getId();

    if (!ownerId.equals(recruiterId)) {
        throw new org.springframework.security.access.AccessDeniedException(
                "You do not own this job");
    }

    // 3. Update and save the status.
    ApplicationStatus oldStatus = application.getStatus();

    application.setStatus(newStatus);

    Application updatedApplication =
            applicationRepository.save(application);

    if (oldStatus != newStatus) {
        notificationService.createNotification(
                application.getCandidate(),
                "Your application for "
                        + application.getJob().getTitle()
                        + " has been updated to "
                        + newStatus,
                "STATUS_UPDATE");
    }

    return updatedApplication;
}

	
	public List<ApplicationResponse> getMyApplications(Long candidateId) {
	
	    List<Application> applications =
	            applicationRepository.findByCandidateId(candidateId);
	
	    return applications.stream()
	            .map(application -> {
	                ApplicationResponse response = new ApplicationResponse();
	
	                response.setApplicationId(application.getId());
	                response.setJobId(application.getJob().getId());
	                response.setJobTitle(application.getJob().getTitle());
	                response.setCompanyName(
	                        application.getJob().getCompanyName());
	                response.setLocation(application.getJob().getLocation());
	                response.setStatus(application.getStatus());
	                response.setAppliedAt(application.getAppliedAt());
	
	                return response;
	            })
	            .toList();
	}
	
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
	        throw new org.springframework.security.access.AccessDeniedException(
	                "You can withdraw only your own application");
	    }

	    ApplicationStatus currentStatus = application.getStatus();

	    if (currentStatus == ApplicationStatus.SELECTED
	            || currentStatus == ApplicationStatus.REJECTED
	            || currentStatus == ApplicationStatus.WITHDRAWN) {

	        throw new IllegalStateException(
	                "Application cannot be withdrawn in its current status");
	    }

	    application.setStatus(ApplicationStatus.WITHDRAWN);

	    Application withdrawnApplication =
	            applicationRepository.save(application);

	    notificationService.createNotification(
	            application.getJob().getRecruiter(),
	            application.getCandidate().getName()
	                    + " withdrew their application for "
	                    + application.getJob().getTitle(),
	            "WITHDRAWAL");

	    return withdrawnApplication;
	}
	
	public List<ApplicationResponse> getApplicantsForJob(
	        Long jobId, Long recruiterId) {

	    // Find the job or throw an exception
	    Job job = jobRepository.findById(jobId)
	            .orElseThrow(() ->
	                    new JobNotFoundException("Job not found"));

	    // Verify that the logged-in recruiter owns this job
	    if (!job.getRecruiter().getId().equals(recruiterId)) {
	        throw new AccessDeniedException(
	                "You are not authorized to view applicants for this job");
	    }

	    // Retrieve all applications for this job
	    List<Application> applications =
	            applicationRepository.findByJobId(jobId);

	    // Convert Application entities into ApplicationResponse DTOs
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
	
	public Map<ApplicationStatus, Long> getApplicationCountsByStatus(
	        Long recruiterId) {

	    Map<ApplicationStatus, Long> counts =
	            new EnumMap<>(ApplicationStatus.class);

	    // Ensure statuses with no applications are also returned as 0
	    for (ApplicationStatus status : ApplicationStatus.values()) {
	        counts.put(status, 0L);
	    }

	    List<Object[]> results =
	            applicationRepository
	                    .countApplicationsByStatusForRecruiter(recruiterId);

	    for (Object[] result : results) {
	        ApplicationStatus status = (ApplicationStatus) result[0];
	        Long count = (Long) result[1];

	        counts.put(status, count);
	    }

	    return counts;
	}
	    
	    
}