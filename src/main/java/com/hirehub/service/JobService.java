
package com.hirehub.service;

import org.springframework.stereotype.Service;
import com.hirehub.exception.JobNotFoundException;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.dto.CreateJobRequest;
import com.hirehub.entity.Job;
import com.hirehub.entity.JobStatus;
import com.hirehub.entity.User;
import com.hirehub.repository.JobRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public Job createJob(CreateJobRequest request, User recruiter) {

        Job job = new Job();

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setVacancies(request.getVacancies());
        job.setSalary(request.getSalary());
        job.setExperienceLevel(request.getExperienceLevel());

        // The server controls the initial status.
        job.setStatus(JobStatus.OPEN);

        // Link the job to the authenticated recruiter.
        job.setRecruiter(recruiter);

        return jobRepository.save(job);
    }
    
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with id: " + id));
    }
}
