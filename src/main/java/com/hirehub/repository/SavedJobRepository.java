package com.hirehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.SavedJob;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    Optional<SavedJob> findByCandidateIdAndJobId(Long candidateId, Long jobId);

    List<SavedJob> findByCandidateIdOrderBySavedAtDesc(Long candidateId);
}