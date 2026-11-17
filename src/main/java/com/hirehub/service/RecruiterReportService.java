
package com.hirehub.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirehub.entity.Application;
import com.hirehub.entity.Interview;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.InterviewRepository;
import com.hirehub.repository.JobRepository;
import com.hirehub.util.ReportExportUtil;

@Service
@Transactional(readOnly = true)
public class RecruiterReportService {

    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final JobRepository jobRepository;

    public RecruiterReportService(
            ApplicationRepository applicationRepository,
            InterviewRepository interviewRepository,
            JobRepository jobRepository) {

        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.jobRepository = jobRepository;
    }

    // Generate application report
    public byte[] exportApplications(
            Long recruiterId,
            Long jobId,
            LocalDate startDate,
            LocalDate endDate,
            String format) {

        validateDateRange(startDate, endDate);

        LocalDateTime startDateTime = startDate == null
                ? null
                : startDate.atStartOfDay();

        LocalDateTime endDateTime = endDate == null
                ? null
                : endDate.atTime(LocalTime.MAX);

        List<Application> applications =
                applicationRepository.findApplicationsForRecruiterReport(
                        recruiterId,
                        jobId,
                        startDateTime,
                        endDateTime
                );

        List<String> headers = List.of(
                "Application ID",
                "Candidate Name",
                "Candidate Email",
                "Job Title",
                "Company Name",
                "Location",
                "Application Status",
                "Applied Date"
        );

        List<List<String>> rows = new ArrayList<>();

        for (Application application : applications) {

            List<String> row = List.of(
                    String.valueOf(application.getId()),
                    safeString(application.getCandidate().getName()),
                    safeString(application.getCandidate().getEmail()),
                    safeString(application.getJob().getTitle()),
                    safeString(application.getJob().getCompanyName()),
                    safeString(application.getJob().getLocation()),
                    application.getStatus().name(),
                    application.getAppliedAt().toString()
            );

            rows.add(row);
        }

        return generateReport(
                "Applications",
                headers,
                rows,
                format
        );
    }

    // Validate optional date range
    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate != null
                && endDate != null
                && endDate.isBefore(startDate)) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }
    }

    // Generate requested file format
    private byte[] generateReport(
            String sheetName,
            List<String> headers,
            List<List<String>> rows,
            String format) {

        if ("csv".equalsIgnoreCase(format)) {
            return ReportExportUtil.generateCsv(headers, rows);
        }

        if ("xlsx".equalsIgnoreCase(format)) {
            try {
                return ReportExportUtil.generateExcel(
                        sheetName,
                        headers,
                        rows
                );
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Unable to generate Excel report",
                        exception
                );
            }
        }

        throw new IllegalArgumentException(
                "Unsupported format. Use csv or xlsx"
        );
    }

    private String safeString(String value) {
        return Objects.toString(value, "");
    }
    

    // Generate interview report
    public byte[] exportInterviews(
            Long recruiterId,
            Long jobId,
            LocalDate startDate,
            LocalDate endDate,
            String format) {

        validateDateRange(startDate, endDate);

        LocalDateTime startDateTime = startDate == null
                ? null
                : startDate.atStartOfDay();

        LocalDateTime endDateTime = endDate == null
                ? null
                : endDate.atTime(LocalTime.MAX);

        List<Interview> interviews =
                interviewRepository.findInterviewsForRecruiterReport(
                        recruiterId,
                        jobId,
                        startDateTime,
                        endDateTime
                );

        List<String> headers = List.of(
                "Interview ID",
                "Candidate Name",
                "Candidate Email",
                "Job Title",
                "Company Name",
                "Round Number",
                "Interview Title",
                "Scheduled Date",
                "Interview Mode",
                "Interview Status",
                "Interview Result"
        );

        List<List<String>> rows = new ArrayList<>();

        for (Interview interview : interviews) {

            List<String> row = List.of(
                    String.valueOf(interview.getId()),
                    safeString(interview.getApplication()
                            .getCandidate().getName()),
                    safeString(interview.getApplication()
                            .getCandidate().getEmail()),
                    safeString(interview.getApplication()
                            .getJob().getTitle()),
                    safeString(interview.getApplication()
                            .getJob().getCompanyName()),
                    String.valueOf(interview.getRoundNumber()),
                    safeString(interview.getInterviewTitle()),
                    interview.getScheduledAt().toString(),
                    interview.getMode().name(),
                    interview.getStatus().name(),
                    interview.getResult() == null
                            ? ""
                            : interview.getResult().name()
            );

            rows.add(row);
        }

        return generateReport(
                "Interviews",
                headers,
                rows,
                format
        );
    }
    

    // Generate job performance report
    public byte[] exportJobs(
            Long recruiterId,
            Long jobId,
            LocalDate startDate,
            LocalDate endDate,
            String format) {

        validateDateRange(startDate, endDate);

        LocalDateTime startDateTime = startDate == null
                ? null
                : startDate.atStartOfDay();

        LocalDateTime endDateTime = endDate == null
                ? null
                : endDate.atTime(LocalTime.MAX);

        List<Object[]> jobs =
                jobRepository.findJobsForRecruiterReport(
                        recruiterId,
                        jobId,
                        startDateTime,
                        endDateTime
                );

        List<String> headers = List.of(
                "Job ID",
                "Job Title",
                "Location",
                "Job Status",
                "Created Date",
                "Deleted",
                "Total Applications"
        );

        List<List<String>> rows = new ArrayList<>();

        for (Object[] job : jobs) {

            List<String> row = List.of(
                    String.valueOf(job[0]),
                    safeString((String) job[1]),
                    safeString((String) job[2]),
                    job[3] == null ? "" : job[3].toString(),
                    job[4] == null ? "" : job[4].toString(),
                    String.valueOf(job[5]),
                    String.valueOf(job[6])
            );

            rows.add(row);
        }

        return generateReport(
                "Job Performance",
                headers,
                rows,
                format
        );
    }


}
