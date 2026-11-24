
package com.hirehub.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.hirehub.entity.InterviewMode;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ScheduleInterviewRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    private ScheduleInterviewRequest createOnlineRequest(String meetingLink) {
        ScheduleInterviewRequest request = new ScheduleInterviewRequest();

        request.setRoundNumber(1);
        request.setInterviewTitle("Technical Interview");
        request.setScheduledAt(LocalDateTime.now().plusDays(1));
        request.setMode(InterviewMode.ONLINE);
        request.setMeetingLink(meetingLink);

        return request;
    }

    @Test
    void validHttpsMeetingLinkShouldPass() {
        ScheduleInterviewRequest request =
                createOnlineRequest("https://meet.example.com/room123");

        Set<ConstraintViolation<ScheduleInterviewRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void validHttpMeetingLinkShouldPass() {
        ScheduleInterviewRequest request =
                createOnlineRequest("http://meet.example.com/room123");

        Set<ConstraintViolation<ScheduleInterviewRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void missingOnlineMeetingLinkShouldFail() {
        ScheduleInterviewRequest request = createOnlineRequest(null);

        Set<ConstraintViolation<ScheduleInterviewRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void malformedOnlineMeetingLinkShouldFail() {
        ScheduleInterviewRequest request = createOnlineRequest("https:// ");

        Set<ConstraintViolation<ScheduleInterviewRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void offlineInterviewWithoutVenueDetailsShouldFail() {
        ScheduleInterviewRequest request = new ScheduleInterviewRequest();

        request.setRoundNumber(1);
        request.setInterviewTitle("Technical Interview");
        request.setScheduledAt(LocalDateTime.now().plusDays(1));
        request.setMode(InterviewMode.OFFLINE);

        Set<ConstraintViolation<ScheduleInterviewRequest>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}
