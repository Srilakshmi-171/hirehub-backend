package com.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hirehub.entity.Application;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);
    List<Application> findByCandidateId(Long candidateId);
}