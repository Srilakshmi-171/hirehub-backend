
package com.hirehub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.CandidatePreference;

public interface CandidatePreferenceRepository
        extends JpaRepository<CandidatePreference, Long> {

    Optional<CandidatePreference> findByCandidateId(Long candidateId);

    boolean existsByCandidateId(Long candidateId);
}
