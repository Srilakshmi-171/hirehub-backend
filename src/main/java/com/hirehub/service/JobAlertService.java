
package com.hirehub.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.entity.Job;
import com.hirehub.entity.JobAlertDelivery;
import com.hirehub.entity.JobStatus;
import com.hirehub.entity.SavedSearch;
import com.hirehub.repository.JobAlertDeliveryRepository;
import com.hirehub.repository.SavedSearchRepository;

@Service
public class JobAlertService {

    private final SavedSearchRepository savedSearchRepository;
    private final JobAlertDeliveryRepository deliveryRepository;
    private final NotificationService notificationService;

    public JobAlertService(
            SavedSearchRepository savedSearchRepository,
            JobAlertDeliveryRepository deliveryRepository,
            NotificationService notificationService) {

        this.savedSearchRepository = savedSearchRepository;
        this.deliveryRepository = deliveryRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void sendJobAlerts(Job job) {

        if (job.getStatus() != JobStatus.OPEN || job.isDeleted()) {
            return;
        }

        var matchingSearches =
                savedSearchRepository.findMatchingSavedSearches(
                        job.getTitle(),
                        job.getLocation()
                );

        Set<Long> notifiedCandidateIds = new HashSet<>();

        for (SavedSearch search : matchingSearches) {

            Long candidateId = search.getCandidate().getId();

            // Avoid processing the same candidate more than once.
            if (!notifiedCandidateIds.add(candidateId)) {
                continue;
            }

            // Avoid duplicate alerts for this candidate and job.
            if (deliveryRepository.existsByCandidateIdAndJobId(
                    candidateId, job.getId())) {
                continue;
            }

            JobAlertDelivery delivery = new JobAlertDelivery();

            delivery.setCandidate(search.getCandidate());
            delivery.setJob(job);

            deliveryRepository.save(delivery);

            notificationService.createNotification(
                    search.getCandidate(),
                    "A new job matching your saved search is available: "
                            + job.getTitle() + " at "
                            + job.getCompanyName(),
                    "JOB_ALERT"
            );
        }
    }
}
