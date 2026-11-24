
package com.hirehub.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hirehub.dto.CandidateResumeResponse;
import com.hirehub.entity.Application;
import com.hirehub.entity.CandidateResume;
import com.hirehub.entity.User;
import com.hirehub.exception.ForbiddenAccessException;
import com.hirehub.exception.InvalidResumeException;
import com.hirehub.exception.ResumeNotFoundException;
import com.hirehub.exception.UserNotFoundException;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.CandidateResumeRepository;
import com.hirehub.repository.UserRepository;

@Service
public class CandidateResumeService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private final CandidateResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public CandidateResumeService(
            CandidateResumeRepository resumeRepository,
            UserRepository userRepository,
            ApplicationRepository applicationRepository) {

        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    // Upload or replace resume
    @Transactional
    public CandidateResumeResponse uploadResume(
            Long candidateId, MultipartFile file) {

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Candidate not found"));

        if (!"CANDIDATE".equalsIgnoreCase(candidate.getRole())) {
            throw new ForbiddenAccessException(
                    "Only candidates can upload resumes");
        }

        validateResume(file);

        try {
            CandidateResume resume =
                    resumeRepository.findByCandidateId(candidateId)
                    .orElseGet(CandidateResume::new);

            String fileName =
                    "resume-" + UUID.randomUUID() + ".pdf";

            resume.setCandidate(candidate);
            resume.setFileName(fileName);
            resume.setContentType("application/pdf");
            resume.setFileData(file.getBytes());
            resume.setUploadedAt(LocalDateTime.now());

            CandidateResume saved =
                    resumeRepository.save(resume);

            return convertToResponse(saved);

        } catch (IOException ex) {
            throw new InvalidResumeException(
                    "Unable to read uploaded resume");
        }
    }

    // Validate uploaded PDF
    private void validateResume(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidResumeException(
                    "Please upload a resume");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidResumeException(
                    "Resume size must not exceed 5 MB");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null ||
                !fileName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {

            throw new InvalidResumeException(
                    "Only PDF files are allowed");
        }

        if (!"application/pdf".equalsIgnoreCase(
                file.getContentType())) {

            throw new InvalidResumeException(
                    "Invalid PDF content type");
        }

        try {
            byte[] data = file.getBytes();

            try (PDDocument document = Loader.loadPDF(data)) {

                if (document.getNumberOfPages() == 0) {
                    throw new InvalidResumeException(
                            "PDF must contain at least one page");
                }

            } catch (IOException ex) {
                throw new InvalidResumeException(
                        "Uploaded file is not a valid PDF");
            }

        } catch (IOException ex) {
            throw new InvalidResumeException(
                    "Unable to validate uploaded PDF");
        }
    }

    // Get resume details without PDF data
    @Transactional(readOnly = true)
    public CandidateResumeResponse getResumeDetails(
            Long candidateId) {

        CandidateResume resume =
                resumeRepository.findByCandidateId(candidateId)
                .orElseThrow(() -> new ResumeNotFoundException(
                        "Resume not found"));

        return convertToResponse(resume);
    }

    // Get actual resume for download
    @Transactional(readOnly = true)
    public CandidateResume getResumeForDownload(
            Long candidateId) {

        return resumeRepository.findByCandidateId(candidateId)
                .orElseThrow(() -> new ResumeNotFoundException(
                        "Resume not found"));
    }

    // Delete resume
    @Transactional
    public void deleteResume(Long candidateId) {

        CandidateResume resume =
                resumeRepository.findByCandidateId(candidateId)
                .orElseThrow(() -> new ResumeNotFoundException(
                        "Resume not found"));

        resumeRepository.delete(resume);
    }

    // Convert entity into response DTO
    private CandidateResumeResponse convertToResponse(
            CandidateResume resume) {

        CandidateResumeResponse response =
                new CandidateResumeResponse();

        response.setResumeId(resume.getId());
        response.setFileName(resume.getFileName());
        response.setContentType(resume.getContentType());
        response.setUploadedAt(resume.getUploadedAt());

        return response;
    }

    // Recruiter downloads candidate resume
    @Transactional(readOnly = true)
    public CandidateResume getResumeForRecruiter(
            Long applicationId, Long recruiterId) {

        Application application =
                applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResumeNotFoundException(
                        "Application not found"));

        if (!application.getJob().getRecruiter()
                .getId().equals(recruiterId)) {

            throw new ForbiddenAccessException(
                    "You are not authorized to access this candidate's resume");
        }

        Long candidateId = application.getCandidate().getId();

        return resumeRepository.findByCandidateId(candidateId)
                .orElseThrow(() -> new ResumeNotFoundException(
                        "Candidate has not uploaded a resume"));
    }
}
