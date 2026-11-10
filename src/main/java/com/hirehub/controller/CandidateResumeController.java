
package com.hirehub.controller;

import com.hirehub.dto.CandidateResumeResponse;
import com.hirehub.entity.CandidateResume;
import com.hirehub.service.CandidateResumeService;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidates/me/resume")
public class CandidateResumeController {

    private final CandidateResumeService resumeService;

    public CandidateResumeController(
            CandidateResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // Upload or replace resume
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CandidateResumeResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        Long candidateId =
                (Long) authentication.getPrincipal();

        CandidateResumeResponse response =
                resumeService.uploadResume(candidateId, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get resume details
    @GetMapping
    public ResponseEntity<CandidateResumeResponse> getResume(
            Authentication authentication) {

        Long candidateId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                resumeService.getResumeDetails(candidateId));
    }

    // Download resume
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadResume(
            Authentication authentication) {

        Long candidateId =
                (Long) authentication.getPrincipal();

        CandidateResume resume =
                resumeService.getResumeForDownload(candidateId);

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

    // Delete resume
    @DeleteMapping
    public ResponseEntity<String> deleteResume(
            Authentication authentication) {

        Long candidateId =
                (Long) authentication.getPrincipal();

        resumeService.deleteResume(candidateId);

        return ResponseEntity.ok(
                "Resume deleted successfully");
    }
    
    @GetMapping("/api/applications/{applicationId}/resume")
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
