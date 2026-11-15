
package com.hirehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hirehub.entity.SavedJob;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    Optional<SavedJob> findByCandidateIdAndJobId(
            Long candidateId, Long jobId);

    List<SavedJob> findByCandidateIdOrderBySavedAtDesc(Long candidateId);

    @Query("""
            SELECT s.job.id
            FROM SavedJob s
            WHERE s.candidate.id = :candidateId
            """)
    List<Long> findSavedJobIdsByCandidateId(
            @Param("candidateId") Long candidateId);
}
