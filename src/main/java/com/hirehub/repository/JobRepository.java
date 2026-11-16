package com.hirehub.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hirehub.entity.Job;
import com.hirehub.entity.JobStatus;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByDeletedFalse();
    
    @Query("""
    	    SELECT j FROM Job j
    	    WHERE j.deleted = false
    	    AND (
    	        (:keyword IS NOT NULL AND
    	         LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
    	        OR
    	        (:location IS NOT NULL AND
    	         LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
    	    )
    	    """)
    	List<Job> searchActiveJobs(
    	        @Param("keyword") String keyword,
    	        @Param("location") String location
    	);
    
    Page<Job> findByDeletedFalse(Pageable pageable);
    
    long countByRecruiterId(Long recruiterId);

    long countByRecruiterIdAndStatus(
            Long recruiterId, JobStatus status);
    
    List<Job> findByDeletedFalseAndStatus(JobStatus status);
    
    @Query("""
    	    SELECT COUNT(j)
    	    FROM Job j
    	    WHERE j.recruiter.id = :recruiterId
    	    AND (:startDate IS NULL OR j.createdAt >= :startDate)
    	    AND (:endDate IS NULL OR j.createdAt <= :endDate)
    	    """)
    	long countTotalJobsForAnalytics(
    	        @Param("recruiterId") Long recruiterId,
    	        @Param("startDate") LocalDateTime startDate,
    	        @Param("endDate") LocalDateTime endDate
    	);

    	@Query("""
    	    SELECT COUNT(j)
    	    FROM Job j
    	    WHERE j.recruiter.id = :recruiterId
    	    AND j.deleted = false
    	    AND j.status = :status
    	    AND (:startDate IS NULL OR j.createdAt >= :startDate)
    	    AND (:endDate IS NULL OR j.createdAt <= :endDate)
    	    """)
    	long countActiveJobsByStatusForAnalytics(
    	        @Param("recruiterId") Long recruiterId,
    	        @Param("status") JobStatus status,
    	        @Param("startDate") LocalDateTime startDate,
    	        @Param("endDate") LocalDateTime endDate
    	);

    	@Query("""
    	    SELECT COUNT(j)
    	    FROM Job j
    	    WHERE j.recruiter.id = :recruiterId
    	    AND j.deleted = true
    	    AND (:startDate IS NULL OR j.createdAt >= :startDate)
    	    AND (:endDate IS NULL OR j.createdAt <= :endDate)
    	    """)
    	long countDeletedJobsForAnalytics(
    	        @Param("recruiterId") Long recruiterId,
    	        @Param("startDate") LocalDateTime startDate,
    	        @Param("endDate") LocalDateTime endDate
    	);
    

}