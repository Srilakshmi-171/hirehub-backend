
package com.hirehub.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hirehub.dto.CompleteInterviewRequest;
import com.hirehub.dto.InterviewFeedbackResponse;
import com.hirehub.dto.InterviewResponse;
import com.hirehub.dto.RescheduleDecisionRequest;
import com.hirehub.dto.RescheduleInterviewRequest;
import com.hirehub.dto.ScheduleInterviewRequest;
import com.hirehub.entity.Application;
import com.hirehub.entity.ApplicationStatus;
import com.hirehub.entity.Interview;
import com.hirehub.entity.InterviewMode;
import com.hirehub.entity.InterviewStatus;
import com.hirehub.entity.User;
import com.hirehub.exception.ApplicationNotFoundException;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.InterviewRepository;
import com.hirehub.repository.UserRepository;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public InterviewService(
            InterviewRepository interviewRepository,
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // Recruiter schedules an interview
    @Transactional
    public InterviewResponse scheduleInterview(
            Long applicationId,
            Long recruiterId,
            ScheduleInterviewRequest request) {

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

        ApplicationStatus applicationStatus = application.getStatus();

        if (applicationStatus != ApplicationStatus.APPLIED
                && applicationStatus != ApplicationStatus.SHORTLISTED) {

            throw new IllegalStateException(
                    "Interviews can only be scheduled for APPLIED "
                    + "or SHORTLISTED applications");
        }

        if (interviewRepository.existsByApplicationIdAndRoundNumber(
                applicationId, request.getRoundNumber())) {

            throw new IllegalStateException(
                    "This interview round already exists");
        }

        // Validate interview mode
        if (request.getMode() == InterviewMode.ONLINE) {

            if (request.getMeetingLink() == null
                    || request.getMeetingLink().isBlank()) {

                throw new IllegalArgumentException(
                        "Meeting link is required for online interviews");
            }

        } else if (request.getMode() == InterviewMode.OFFLINE) {

            if (request.getVenueName() == null
                    || request.getVenueName().isBlank()
                    || request.getVenueAddress() == null
                    || request.getVenueAddress().isBlank()) {

                throw new IllegalArgumentException(
                        "Venue name and address are required "
                        + "for offline interviews");
            }
        }

        Interview interview = new Interview();

        interview.setApplication(application);
        interview.setRoundNumber(request.getRoundNumber());
        interview.setInterviewTitle(request.getInterviewTitle());
        interview.setScheduledAt(request.getScheduledAt());
        interview.setMode(request.getMode());
        interview.setMeetingLink(request.getMeetingLink());
        interview.setVenueName(request.getVenueName());
        interview.setVenueAddress(request.getVenueAddress());
        interview.setInstructions(request.getInstructions());

        Interview savedInterview = interviewRepository.save(interview);

        notificationService.createNotification(
                application.getCandidate(),
                "You have been invited to "
                        + request.getInterviewTitle()
                        + " (Round " + request.getRoundNumber() + ") "
                        + "for " + application.getJob().getTitle(),
                "INTERVIEW");

        return convertToResponse(savedInterview);
    }

    // Get all interview rounds for an application
    @Transactional(readOnly = true)
    public List<InterviewResponse> getInterviewsForApplication(
            Long applicationId,
            Long userId) {

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found with ID: " + applicationId));

        Long candidateId = application.getCandidate().getId();

        Long recruiterId = application.getJob()
                .getRecruiter()
                .getId();

        if (!candidateId.equals(userId)
                && !recruiterId.equals(userId)) {

            throw new AccessDeniedException(
                    "You are not authorized to view these interviews");
        }

        return interviewRepository
                .findByApplicationIdOrderByRoundNumberAsc(applicationId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Convert entity into response DTO
    private InterviewResponse convertToResponse(Interview interview) {

        InterviewResponse response = new InterviewResponse();

        response.setId(interview.getId());
        response.setApplicationId(
                interview.getApplication().getId());
        response.setCandidateName(
                interview.getApplication().getCandidate().getName());
        response.setRoundNumber(interview.getRoundNumber());
        response.setInterviewTitle(interview.getInterviewTitle());
        response.setScheduledAt(interview.getScheduledAt());
        response.setMode(interview.getMode());
        response.setMeetingLink(interview.getMeetingLink());
        response.setVenueName(interview.getVenueName());
        response.setVenueAddress(interview.getVenueAddress());
        response.setInstructions(interview.getInstructions());
        response.setStatus(interview.getStatus());
        response.setResult(interview.getResult());
        response.setCreatedAt(interview.getCreatedAt());
        response.setUpdatedAt(interview.getUpdatedAt());
        response.setRequestedNewTime(interview.getRequestedNewTime());
        response.setRescheduleReason(interview.getRescheduleReason());

        // Recruiter feedback is intentionally not exposed here.

        return response;
    }

    // Candidate accepts or rejects an interview
    @Transactional
    public InterviewResponse respondToInterview(
            Long interviewId,
            Long candidateId,
            InterviewStatus response) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interview not found with ID: " + interviewId));

        Long ownerId = interview.getApplication()
                .getCandidate()
                .getId();

        if (!ownerId.equals(candidateId)) {
            throw new AccessDeniedException(
                    "You are not authorized to respond to this interview");
        }

        if (response != InterviewStatus.ACCEPTED
                && response != InterviewStatus.REJECTED) {

            throw new IllegalArgumentException(
                    "Response must be ACCEPTED or REJECTED");
        }

        if (interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "This interview cannot be responded to in its current status");
        }

        interview.setStatus(response);

        Interview updatedInterview = interviewRepository.save(interview);

        User recruiter = interview.getApplication()
                .getJob()
                .getRecruiter();

        notificationService.createNotification(
                recruiter,
                "Candidate "
                        + interview.getApplication().getCandidate().getName()
                        + " has " + response.name().toLowerCase()
                        + " the interview: " + interview.getInterviewTitle(),
                "INTERVIEW");

        return convertToResponse(updatedInterview);
    }

    // Candidate requests rescheduling
    @Transactional
    public InterviewResponse requestReschedule(
            Long interviewId,
            Long candidateId,
            RescheduleInterviewRequest request) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interview not found with ID: " + interviewId));

        Long ownerId = interview.getApplication()
                .getCandidate()
                .getId();

        if (!ownerId.equals(candidateId)) {
            throw new AccessDeniedException(
                    "You are not authorized to request rescheduling for this interview");
        }

        if (interview.getStatus() != InterviewStatus.SCHEDULED
                && interview.getStatus() != InterviewStatus.ACCEPTED) {

            throw new IllegalStateException(
                    "Rescheduling cannot be requested in the current interview status");
        }

        // FIX 1: Save the previous status before changing it
        interview.setStatusBeforeReschedule(interview.getStatus());

        interview.setRequestedNewTime(request.getRequestedNewTime());
        interview.setRescheduleReason(request.getRescheduleReason());
        interview.setStatus(InterviewStatus.RESCHEDULE_REQUESTED);

        Interview updatedInterview = interviewRepository.save(interview);

        User recruiter = interview.getApplication()
                .getJob()
                .getRecruiter();

        notificationService.createNotification(
                recruiter,
                "Candidate " + interview.getApplication().getCandidate().getName()
                        + " requested to reschedule the interview: "
                        + interview.getInterviewTitle(),
                "INTERVIEW");

        return convertToResponse(updatedInterview);
    }

    // Recruiter approves or rejects rescheduling
    @Transactional
    public InterviewResponse decideReschedule(
            Long interviewId,
            Long recruiterId,
            RescheduleDecisionRequest request) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interview not found with ID: " + interviewId));

        Long ownerId = interview.getApplication()
                .getJob()
                .getRecruiter()
                .getId();

        if (!ownerId.equals(recruiterId)) {
            throw new AccessDeniedException(
                    "You are not authorized to manage this interview");
        }

        if (interview.getStatus() != InterviewStatus.RESCHEDULE_REQUESTED) {
            throw new IllegalStateException(
                    "There is no pending rescheduling request");
        }

        User candidate = interview.getApplication().getCandidate();

        if (Boolean.TRUE.equals(request.getApproved())) {

            interview.setScheduledAt(interview.getRequestedNewTime());
            interview.setStatus(InterviewStatus.SCHEDULED);

            notificationService.createNotification(
                    candidate,
                    "Your rescheduling request for "
                            + interview.getInterviewTitle()
                            + " has been approved. The new interview time is "
                            + interview.getScheduledAt(),
                    "INTERVIEW");

        } else {

            // FIX 2: Handle older requests with a null previous status
            InterviewStatus previousStatus =
                    interview.getStatusBeforeReschedule();

            if (previousStatus == null) {
                previousStatus = InterviewStatus.SCHEDULED;
            }

            interview.setStatus(previousStatus);

            notificationService.createNotification(
                    candidate,
                    "Your rescheduling request for "
                            + interview.getInterviewTitle()
                            + " has been rejected. The original interview schedule remains unchanged.",
                    "INTERVIEW");
        }

        // Clear pending rescheduling information
        interview.setRequestedNewTime(null);
        interview.setRescheduleReason(null);
        interview.setStatusBeforeReschedule(null);

        Interview updatedInterview = interviewRepository.save(interview);

        return convertToResponse(updatedInterview);
    }
    
    @Transactional
    public InterviewResponse cancelInterview(Long interviewId, Long recruiterId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Interview not found"));

        if (!interview.getApplication().getJob()
                .getRecruiter().getId().equals(recruiterId)) {
            throw new AccessDeniedException(
                    "You are not authorized to cancel this interview");
        }

        if (interview.getStatus() == InterviewStatus.CANCELLED
                || interview.getStatus() == InterviewStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cancelled or completed interviews cannot be cancelled");
        }

        interview.setStatus(InterviewStatus.CANCELLED);

        interviewRepository.save(interview);

        User candidate = interview.getApplication().getCandidate();

        notificationService.createNotification(
                candidate,
                "Your interview " + interview.getInterviewTitle()
                        + " has been cancelled by the recruiter.",
                "INTERVIEW"
        );

        return convertToResponse(interview);
    }
    
    @Transactional
    public InterviewResponse completeInterview(
            Long interviewId,
            Long recruiterId,
            CompleteInterviewRequest request) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Interview not found"));

        if (!interview.getApplication().getJob()
                .getRecruiter().getId().equals(recruiterId)) {
            throw new AccessDeniedException(
                    "You are not authorized to complete this interview");
        }

        if (interview.getStatus() == InterviewStatus.CANCELLED
                || interview.getStatus() == InterviewStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cancelled or already completed interviews cannot be completed");
        }

        interview.setStatus(InterviewStatus.COMPLETED);
        interview.setRecruiterFeedback(request.getRecruiterFeedback());
        interview.setResult(request.getResult());

        interviewRepository.save(interview);

        User candidate = interview.getApplication().getCandidate();

        notificationService.createNotification(
                candidate,
                "Your " + interview.getInterviewTitle()
                        + " interview outcome is " + request.getResult(),
                "INTERVIEW"
        );

        return convertToResponse(interview);
    }
    
    @Transactional(readOnly = true)
    public InterviewFeedbackResponse getInterviewFeedback(
            Long interviewId, Long recruiterId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Interview not found"));

        if (!interview.getApplication().getJob()
                .getRecruiter().getId().equals(recruiterId)) {
            throw new AccessDeniedException(
                    "You are not authorized to view this interview feedback");
        }

        InterviewFeedbackResponse response =
                new InterviewFeedbackResponse();

        response.setInterviewId(interview.getId());
        response.setApplicationId(
                interview.getApplication().getId());
        response.setInterviewTitle(interview.getInterviewTitle());
        response.setRecruiterFeedback(
                interview.getRecruiterFeedback());
        response.setResult(interview.getResult());
        response.setStatus(interview.getStatus());

        return response;
    }
    
}
