
package com.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.JobAlertDelivery;

public interface JobAlertDeliveryRepository
        extends JpaRepository<JobAlertDelivery, Long> {

    boolean existsByCandidateIdAndJobId(
            Long candidateId,
            Long jobId
    );
}
