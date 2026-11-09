
package com.hirehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.ApplicationStatusHistory;

public interface ApplicationStatusHistoryRepository
        extends JpaRepository<ApplicationStatusHistory, Long> {

    List<ApplicationStatusHistory>
        findByApplicationIdOrderByChangedAtDesc(Long applicationId);
}
