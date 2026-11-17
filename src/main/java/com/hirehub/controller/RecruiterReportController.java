
package com.hirehub.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hirehub.service.RecruiterReportService;

@RestController
@RequestMapping("/api/recruiter/reports")
public class RecruiterReportController {

    private final RecruiterReportService reportService;

    public RecruiterReportController(
            RecruiterReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/applications")
    public ResponseEntity<byte[]> exportApplications(
            Authentication authentication,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(defaultValue = "csv") String format) {

        Long recruiterId = (Long) authentication.getPrincipal();

        byte[] file = reportService.exportApplications(
                recruiterId, jobId, startDate, endDate, format);

        return createFileResponse(file, "applications", format);
    }

    @GetMapping("/interviews")
    public ResponseEntity<byte[]> exportInterviews(
            Authentication authentication,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(defaultValue = "csv") String format) {

        Long recruiterId = (Long) authentication.getPrincipal();

        byte[] file = reportService.exportInterviews(
                recruiterId, jobId, startDate, endDate, format);

        return createFileResponse(file, "interviews", format);
    }

    @GetMapping("/jobs")
    public ResponseEntity<byte[]> exportJobs(
            Authentication authentication,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(defaultValue = "csv") String format) {

        Long recruiterId = (Long) authentication.getPrincipal();

        byte[] file = reportService.exportJobs(
                recruiterId, jobId, startDate, endDate, format);

        return createFileResponse(file, "jobs", format);
    }

    private ResponseEntity<byte[]> createFileResponse(
            byte[] file,
            String reportName,
            String format) {

        String extension = format.toLowerCase();

        MediaType mediaType = "xlsx".equals(extension)
                ? MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                : MediaType.parseMediaType("text/csv");

        String filename = reportName + "-report." + extension;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .body(file);
    }
}
