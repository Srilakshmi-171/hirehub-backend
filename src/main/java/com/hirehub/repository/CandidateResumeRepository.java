
package com.hirehub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.CandidateResume;

public interface CandidateResumeRepository
        extends JpaRepository<CandidateResume, Long> {

    Optional<CandidateResume> findByCandidateId(Long candidateId);

    boolean existsByCandidateId(Long candidateId);

    void deleteByCandidateId(Long candidateId);
}
