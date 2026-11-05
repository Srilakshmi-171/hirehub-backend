package com.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hirehub.entity.Application;
import com.hirehub.entity.ApplicationStatus;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    boolean existsByCandidateIdAndJobId(
            Long candidateId, Long jobId);

    List<Application> findByCandidateId(Long candidateId);

    List<Application> findByJobId(Long jobId);

    // Recruiter dashboard queries
    long countByJobRecruiterId(Long recruiterId);

    long countByJobRecruiterIdAndStatus(
            Long recruiterId, ApplicationStatus status);

    @Query("""
            SELECT a.status, COUNT(a)
            FROM Application a
            WHERE a.job.recruiter.id = :recruiterId
            GROUP BY a.status
            """)
    List<Object[]> countApplicationsByStatusForRecruiter(
            @Param("recruiterId") Long recruiterId);

    // Candidate dashboard queries
    long countByCandidateId(Long candidateId);

    @Query("""
            SELECT a.status, COUNT(a)
            FROM Application a
            WHERE a.candidate.id = :candidateId
            GROUP BY a.status
            """)
    List<Object[]> countApplicationsByStatusForCandidate(
            @Param("candidateId") Long candidateId);

    List<Application> findTop5ByCandidateIdOrderByAppliedAtDesc(
            Long candidateId);
}