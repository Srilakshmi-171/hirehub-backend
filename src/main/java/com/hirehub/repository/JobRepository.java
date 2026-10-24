package com.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hirehub.entity.Job;

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

}