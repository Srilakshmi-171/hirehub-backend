package com.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByDeletedFalse();

}