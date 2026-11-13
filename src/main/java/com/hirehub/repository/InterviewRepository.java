
package com.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.Interview;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationIdOrderByRoundNumberAsc(Long applicationId);

    boolean existsByApplicationIdAndRoundNumber(Long applicationId, Integer roundNumber);
}
