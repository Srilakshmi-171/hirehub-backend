
package com.hirehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hirehub.entity.SavedSearch;

public interface SavedSearchRepository
        extends JpaRepository<SavedSearch, Long> {

    // Get all saved searches belonging to a candidate
    List<SavedSearch> findByCandidateIdOrderByCreatedAtDesc(
            Long candidateId);

    // Find a saved search only if it belongs to that candidate
    Optional<SavedSearch> findByIdAndCandidateId(
            Long id, Long candidateId);
    
	
	@Query("""
	    SELECT s
	    FROM SavedSearch s
	    JOIN FETCH s.candidate c
	    WHERE c.role = 'CANDIDATE'
	      AND c.accountStatus = com.hirehub.entity.AccountStatus.ACTIVE
	      AND (
	          (s.keyword IS NOT NULL
	           AND LOWER(:title) LIKE
	               CONCAT('%', LOWER(s.keyword), '%'))
	          OR
	          (s.location IS NOT NULL
	           AND LOWER(:location) LIKE
	               CONCAT('%', LOWER(s.location), '%'))
	      )
	    """)
	List<SavedSearch> findMatchingSavedSearches(
	        @Param("title") String title,
	        @Param("location") String location
	);

}
