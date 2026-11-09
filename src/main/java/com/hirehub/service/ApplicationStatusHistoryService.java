
package com.hirehub.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.dto.ApplicationStatusHistoryResponse;
import com.hirehub.entity.Application;
import com.hirehub.entity.ApplicationStatusHistory;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.ApplicationStatusHistoryRepository;
import com.hirehub.exception.ApplicationNotFoundException;

@Service
public class ApplicationStatusHistoryService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;

    public ApplicationStatusHistoryService(
            ApplicationRepository applicationRepository,
            ApplicationStatusHistoryRepository historyRepository) {

        this.applicationRepository = applicationRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional(readOnly = true)
    public List<ApplicationStatusHistoryResponse> getApplicationHistory(
            Long applicationId, Long userId) {

        // 1. Find the application
        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Application not found with ID: " + applicationId));

        // 2. Get candidate and recruiter IDs
        Long candidateId = application.getCandidate().getId();

        Long recruiterId = application.getJob()
                .getRecruiter()
                .getId();

        // 3. Verify ownership
        if (!candidateId.equals(userId)
                && !recruiterId.equals(userId)) {

            throw new AccessDeniedException(
                    "You are not authorized to view this application history");
        }

        // 4. Retrieve and convert history records
        return historyRepository
                .findByApplicationIdOrderByChangedAtDesc(applicationId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ApplicationStatusHistoryResponse convertToResponse(
            ApplicationStatusHistory history) {

        ApplicationStatusHistoryResponse response =
                new ApplicationStatusHistoryResponse();

        response.setHistoryId(history.getId());
        response.setApplicationId(history.getApplication().getId());
        response.setPreviousStatus(history.getPreviousStatus());
        response.setNewStatus(history.getNewStatus());
        response.setChangedBy(history.getChangedBy().getId());
        response.setChangedAt(history.getChangedAt());
        response.setRemarks(history.getRemarks());

        return response;
    }
}
