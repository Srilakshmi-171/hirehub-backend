
package com.hirehub.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.dto.JobRecommendationResponse;
import com.hirehub.entity.AccountStatus;
import com.hirehub.entity.CandidatePreference;
import com.hirehub.entity.Job;
import com.hirehub.entity.JobStatus;
import com.hirehub.entity.User;
import com.hirehub.exception.CandidatePreferenceNotFoundException;
import com.hirehub.exception.ForbiddenAccessException;
import com.hirehub.exception.UserNotFoundException;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.CandidatePreferenceRepository;
import com.hirehub.repository.JobRepository;
import com.hirehub.repository.SavedJobRepository;
import com.hirehub.repository.UserRepository;
import com.hirehub.util.ExperienceMatcher;
import com.hirehub.util.LocationMatcher;
import com.hirehub.util.SkillMatcher;
import com.hirehub.util.SkillNormalizer;

@Service
public class JobRecommendationService {

    private final CandidatePreferenceRepository preferenceRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;

    public JobRecommendationService(
            CandidatePreferenceRepository preferenceRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            SavedJobRepository savedJobRepository,
            UserRepository userRepository) {

        this.preferenceRepository = preferenceRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.savedJobRepository = savedJobRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<JobRecommendationResponse> getRecommendations(
            Long candidateId) {

        validateCandidate(candidateId);

        CandidatePreference preference =
                preferenceRepository.findByCandidateId(candidateId)
                .orElseThrow(() ->
                        new CandidatePreferenceNotFoundException(
                                "Candidate preferences not found"));

        List<Job> jobs =
                jobRepository.findByDeletedFalseAndStatus(JobStatus.OPEN);

        Set<Long> appliedJobIds = new java.util.HashSet<>(
                applicationRepository.findAppliedJobIdsByCandidateId(
                        candidateId));

        Set<Long> savedJobIds = new java.util.HashSet<>(
                savedJobRepository.findSavedJobIdsByCandidateId(
                        candidateId));

        LocalDateTime sevenDaysAgo =
                LocalDateTime.now().minusDays(7);

        List<JobRecommendationResponse> recommendations =
                new ArrayList<>();

        for (Job job : jobs) {

            int skillsScore = SkillMatcher.calculateScore(
                    preference.getSkills(),
                    job.getRequiredSkills());

            int locationScore = LocationMatcher.calculateScore(
                    preference.getPreferredLocations(),
                    preference.isRemotePreference(),
                    job.getLocation());

            int experienceScore = ExperienceMatcher.calculateScore(
                    preference.getExperienceLevel(),
                    job.getExperienceLevel());

            int matchPercentage =
                    skillsScore + locationScore + experienceScore;

            JobRecommendationResponse response =
                    new JobRecommendationResponse();

            response.setJobId(job.getId());
            response.setTitle(job.getTitle());
            response.setCompanyName(job.getCompanyName());
            response.setLocation(job.getLocation());

            response.setRequiredSkills(
                    SkillNormalizer.normalize(job.getRequiredSkills())
                            .stream()
                            .toList());

            response.setSalary(job.getSalary());
            response.setExperienceLevel(job.getExperienceLevel());
            response.setCreatedAt(job.getCreatedAt());

            response.setSkillsScore(skillsScore);
            response.setLocationScore(locationScore);
            response.setExperienceScore(experienceScore);
            response.setMatchPercentage(matchPercentage);

            response.setApplied(appliedJobIds.contains(job.getId()));
            response.setSaved(savedJobIds.contains(job.getId()));

            response.setNew(
                    job.getCreatedAt() != null
                    && !job.getCreatedAt().isBefore(sevenDaysAgo));

            recommendations.add(response);
        }

        recommendations.sort(
                Comparator.comparingInt(
                        JobRecommendationResponse::getMatchPercentage)
                .reversed()
                .thenComparing(
                        JobRecommendationResponse::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder()))
        );

        return recommendations;
    }

    private void validateCandidate(Long candidateId) {

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Candidate not found"));

        if (!"CANDIDATE".equals(candidate.getRole())) {
            throw new ForbiddenAccessException(
                    "Only candidates can view job recommendations");
        }

        if (candidate.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ForbiddenAccessException(
                    "Inactive candidates cannot view job recommendations");
        }
    }
}
