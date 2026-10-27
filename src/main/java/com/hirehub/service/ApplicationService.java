package com.hirehub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.entity.Application;
import com.hirehub.entity.ApplicationStatus;
import com.hirehub.entity.Job;
import com.hirehub.entity.JobStatus;
import com.hirehub.entity.User;
import com.hirehub.exception.ApplicationNotFoundException;
import com.hirehub.exception.DuplicateApplicationException;
import com.hirehub.exception.JobNotOpenException;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.UserRepository;


@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobService jobService;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobService jobService) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobService = jobService;
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

        return applicationRepository.save(application);
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
    application.setStatus(newStatus);

    return applicationRepository.save(application);
}

    
    
}