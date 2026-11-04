package com.hirehub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.RecruiterProfile;

public interface RecruiterProfileRepository
        extends JpaRepository<RecruiterProfile, Long> {

    Optional<RecruiterProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}