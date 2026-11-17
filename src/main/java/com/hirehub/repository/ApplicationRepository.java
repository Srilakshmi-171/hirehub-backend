package com.hirehub.repository;

import java.time.LocalDateTime;
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
    
    @Query("""
            SELECT a.job.id
            FROM Application a
            WHERE a.candidate.id = :candidateId
            """)
    List<Long> findAppliedJobIdsByCandidateId(
            @Param("candidateId") Long candidateId);
    
	
	@Query("""
	        SELECT a.status, COUNT(a)
	        FROM Application a
	        WHERE a.job.recruiter.id = :recruiterId
	        AND (:startDate IS NULL OR a.appliedAt >= :startDate)
	        AND (:endDate IS NULL OR a.appliedAt <= :endDate)
	        GROUP BY a.status
	        """)
	List<Object[]> countApplicationsByStatusForAnalytics(
	        @Param("recruiterId") Long recruiterId,
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate
	);
	
	@Query("""
	        SELECT a.job.id, a.job.title, COUNT(a)
	        FROM Application a
	        WHERE a.job.recruiter.id = :recruiterId
	        AND a.job.deleted = false
	        AND (:startDate IS NULL OR a.appliedAt >= :startDate)
	        AND (:endDate IS NULL OR a.appliedAt <= :endDate)
	        GROUP BY a.job.id, a.job.title
	        ORDER BY COUNT(a) DESC
	        """)
	List<Object[]> findTopJobsByApplicationsForAnalytics(
	        @Param("recruiterId") Long recruiterId,
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate,
	        org.springframework.data.domain.Pageable pageable
	);


    // Recruiter application report query
    @Query("""
            SELECT a
            FROM Application a
            JOIN FETCH a.candidate
            JOIN FETCH a.job
            WHERE a.job.recruiter.id = :recruiterId
            AND (:jobId IS NULL OR a.job.id = :jobId)
            AND (:startDate IS NULL OR a.appliedAt >= :startDate)
            AND (:endDate IS NULL OR a.appliedAt <= :endDate)
            ORDER BY a.appliedAt DESC
            """)
    List<Application> findApplicationsForRecruiterReport(
            @Param("recruiterId") Long recruiterId,
            @Param("jobId") Long jobId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}