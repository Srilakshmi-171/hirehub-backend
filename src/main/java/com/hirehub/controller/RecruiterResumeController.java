package com.hirehub.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.entity.CandidateResume;
import com.hirehub.service.CandidateResumeService;

@RestController
@RequestMapping("/api/applications")
public class RecruiterResumeController {

    private final CandidateResumeService resumeService;

    public RecruiterResumeController(CandidateResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping("/{applicationId}/resume")
    public ResponseEntity<byte[]> downloadResumeForRecruiter(
            @PathVariable Long applicationId,
            Authentication authentication) {

        Long recruiterId = (Long) authentication.getPrincipal();

        CandidateResume resume = resumeService.getResumeForRecruiter(
                applicationId, recruiterId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(resume.getFileName())
                        .build());

        return new ResponseEntity<>(
                resume.getFileData(),
                headers,
                HttpStatus.OK);
    }
}