
package com.hirehub.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.hirehub.dto.ApiErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            WebRequest request,
            Map<String, String> validationErrors) {

        String path = ((ServletWebRequest) request)
                .getRequest()
                .getRequestURI();

        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                validationErrors
        );

        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            WebRequest request) {

        return buildErrorResponse(
                status,
                message,
                request,
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error :
                exception.getBindingResult().getFieldErrors()) {

            errors.put(error.getField(), error.getDefaultMessage());
        }

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                errors
        );
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailException(
            DuplicateEmailException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(
            UserNotFoundException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidUserException(
            InvalidUserIdException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied",
                request
        );
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleJobNotFoundException(
            JobNotFoundException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateApplication(
            DuplicateApplicationException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(JobNotOpenException.class)
    public ResponseEntity<ApiErrorResponse> handleJobNotOpen(
            JobNotOpenException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleApplicationNotFound(
            ApplicationNotFoundException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(RecruiterProfileNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRecruiterProfileNotFound(
            RecruiterProfileNotFoundException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(RecruiterProfileAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleRecruiterProfileAlreadyExists(
            RecruiterProfileAlreadyExistsException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleForbiddenAccess(
            ForbiddenAccessException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ResumeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResumeNotFound(
            ResumeNotFoundException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidResumeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidResume(
            InvalidResumeException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(CandidatePreferenceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCandidatePreferenceNotFound(
            CandidatePreferenceNotFoundException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(CandidatePreferenceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleCandidatePreferenceAlreadyExists(
            CandidatePreferenceAlreadyExistsException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(SavedSearchNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSavedSearchNotFound(
            SavedSearchNotFoundException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception exception,
            WebRequest request) {

        ServletWebRequest servletRequest = (ServletWebRequest) request;

        logger.error(
                "Unexpected error occurred while processing request: {} {}",
                servletRequest.getRequest().getMethod(),
                servletRequest.getRequest().getRequestURI(),
                exception
        );

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            ResponseStatusException exception,
            WebRequest request) {

        HttpStatus status = HttpStatus.valueOf(
                exception.getStatusCode().value()
        );

        String message = exception.getReason() != null
                ? exception.getReason()
                : status.getReasonPhrase();

        return buildErrorResponse(
                status,
                message,
                request
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException exception,
            WebRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Resume size must not exceed 5 MB",
                request
        );
    }
}
