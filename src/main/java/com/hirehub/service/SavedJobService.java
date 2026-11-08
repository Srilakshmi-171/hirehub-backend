package com.hirehub.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hirehub.entity.Job;
import com.hirehub.entity.JobStatus;
import com.hirehub.entity.SavedJob;
import com.hirehub.entity.User;
import com.hirehub.repository.JobRepository;
import com.hirehub.repository.SavedJobRepository;
import com.hirehub.repository.UserRepository;
import java.util.stream.Collectors;
import com.hirehub.dto.SavedJobResponse;

@Service
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public SavedJobService(
            SavedJobRepository savedJobRepository,
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.savedJobRepository = savedJobRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // Save a job
    public SavedJob saveJob(Long candidateId, Long jobId) {

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Candidate not found"));

        if (!"CANDIDATE".equals(candidate.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only candidates can save jobs");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job not found"));

        if (job.isDeleted() || job.getStatus() != JobStatus.OPEN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This job is not available for saving");
        }

        if (savedJobRepository.existsByCandidateIdAndJobId(
                candidateId, jobId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job is already saved");
        }

        SavedJob savedJob = new SavedJob();
        savedJob.setCandidate(candidate);
        savedJob.setJob(job);

        return savedJobRepository.save(savedJob);
    }

    // Get all saved jobs
    public List<SavedJobResponse> getSavedJobs(Long candidateId) {

        return savedJobRepository
                .findByCandidateIdOrderBySavedAtDesc(candidateId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private SavedJobResponse convertToResponse(SavedJob savedJob) {

        SavedJobResponse response = new SavedJobResponse();

        response.setSavedJobId(savedJob.getId());
        response.setJobId(savedJob.getJob().getId());
        response.setJobTitle(savedJob.getJob().getTitle());
        response.setCompanyName(savedJob.getJob().getCompanyName());
        response.setLocation(savedJob.getJob().getLocation());
        response.setSavedAt(savedJob.getSavedAt());

        return response;
    }
    
    // Remove a saved job
    public void removeSavedJob(Long candidateId, Long jobId) {

        SavedJob savedJob = savedJobRepository
                .findByCandidateIdAndJobId(candidateId, jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Saved job not found"));

        savedJobRepository.delete(savedJob);
    }
}