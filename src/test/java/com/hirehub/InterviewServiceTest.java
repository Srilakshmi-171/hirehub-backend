package com.hirehub;

import com.hirehub.repository.InterviewRepository;
import com.hirehub.repository.ApplicationRepository;
import com.hirehub.repository.UserRepository;
import com.hirehub.service.InterviewService;
import com.hirehub.service.NotificationService;
import com.hirehub.service.EmailService;
import com.hirehub.service.ApplicationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hirehub.entity.Application;
import com.hirehub.entity.ApplicationStatus;
import com.hirehub.entity.Interview;
import com.hirehub.entity.InterviewStatus;
import com.hirehub.entity.Job;
import com.hirehub.entity.User;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private ApplicationService applicationService;
    @InjectMocks
    private InterviewService interviewService;
    private Interview createInterview(ApplicationStatus applicationStatus) {
        User recruiter = new User();
        recruiter.setId(302L);

        Job job = new Job();
        job.setRecruiter(recruiter);

        User candidate = new User();
        candidate.setId(752L);

        Application application = new Application();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setStatus(applicationStatus);

        Interview interview = new Interview();
        interview.setId(10L);
        interview.setApplication(application);
        interview.setStatus(InterviewStatus.ACCEPTED);
        interview.setScheduledAt(
                java.time.LocalDateTime.now().minusHours(1));

        return interview;
    }
    @Test
    void completeInterview_shouldRejectRejectedApplication() {

        Interview interview = createInterview(ApplicationStatus.REJECTED);

        when(interviewRepository.findById(10L))
                .thenReturn(java.util.Optional.of(interview));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> interviewService.completeInterview(10L, 302L, null)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals(
                "Cannot complete an interview for a rejected or withdrawn application",
                exception.getReason()
        );

        verify(interviewRepository, never()).save(any(Interview.class));
        verifyNoInteractions(notificationService);
    }

    @Test
    void completeInterview_shouldRejectWithdrawnApplication() {

        Interview interview = createInterview(ApplicationStatus.WITHDRAWN);

        when(interviewRepository.findById(10L))
                .thenReturn(java.util.Optional.of(interview));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> interviewService.completeInterview(10L, 302L, null)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals(
                "Cannot complete an interview for a rejected or withdrawn application",
                exception.getReason()
        );

        verify(interviewRepository, never()).save(any(Interview.class));
        verifyNoInteractions(notificationService);
    }

}