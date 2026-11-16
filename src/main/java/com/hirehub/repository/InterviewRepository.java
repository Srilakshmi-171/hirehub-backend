
package com.hirehub.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hirehub.entity.InterviewStatus;
import com.hirehub.entity.InterviewResult;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.Interview;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationIdOrderByRoundNumberAsc(Long applicationId);

    boolean existsByApplicationIdAndRoundNumber(Long applicationId, Integer roundNumber);
    
	
	@Query("""
	        SELECT i.status, COUNT(i)
	        FROM Interview i
	        WHERE i.application.job.recruiter.id = :recruiterId
	        AND (:startDate IS NULL OR i.scheduledAt >= :startDate)
	        AND (:endDate IS NULL OR i.scheduledAt <= :endDate)
	        GROUP BY i.status
	        """)
	List<Object[]> countInterviewsByStatusForAnalytics(
	        @Param("recruiterId") Long recruiterId,
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate
	);
	
	@Query("""
	        SELECT i.result, COUNT(i)
	        FROM Interview i
	        WHERE i.application.job.recruiter.id = :recruiterId
	        AND i.result IS NOT NULL
	        AND (:startDate IS NULL OR i.scheduledAt >= :startDate)
	        AND (:endDate IS NULL OR i.scheduledAt <= :endDate)
	        GROUP BY i.result
	        """)
	List<Object[]> countInterviewsByResultForAnalytics(
	        @Param("recruiterId") Long recruiterId,
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate
	);
	
	@Query("""
	        SELECT COUNT(i)
	        FROM Interview i
	        WHERE i.application.job.recruiter.id = :recruiterId
	        AND (:startDate IS NULL OR i.scheduledAt >= :startDate)
	        AND (:endDate IS NULL OR i.scheduledAt <= :endDate)
	        """)
	long countTotalInterviewsForAnalytics(
	        @Param("recruiterId") Long recruiterId,
	        @Param("startDate") LocalDateTime startDate,
	        @Param("endDate") LocalDateTime endDate
	);

}
