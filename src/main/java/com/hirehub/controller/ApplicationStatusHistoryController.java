
package com.hirehub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.dto.ApplicationStatusHistoryResponse;
import com.hirehub.service.ApplicationStatusHistoryService;

@RestController
@RequestMapping("/api/applications")
public class ApplicationStatusHistoryController {

    private final ApplicationStatusHistoryService historyService;

    public ApplicationStatusHistoryController(
            ApplicationStatusHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/{applicationId}/history")
    public ResponseEntity<List<ApplicationStatusHistoryResponse>>
            getApplicationHistory(
                    @PathVariable Long applicationId,
                    Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        List<ApplicationStatusHistoryResponse> history =
                historyService.getApplicationHistory(
                        applicationId, userId);

        return ResponseEntity.ok(history);
    }
}
