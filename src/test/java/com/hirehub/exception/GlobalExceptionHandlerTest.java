
package com.hirehub.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import com.hirehub.dto.ApiErrorResponse;

class GlobalExceptionHandlerTest {

    @Test
    void unexpectedExceptionShouldReturnGenericErrorResponse() {

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI("/api/test");

        ServletWebRequest webRequest =
                new ServletWebRequest(request);

        Exception exception =
                new RuntimeException("Database password leaked");

        var response =
                handler.handleUnexpectedException(exception, webRequest);

        assertEquals(500, response.getStatusCode().value());

        ApiErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals("An unexpected error occurred", body.message());
        assertEquals("/api/test", body.path());
        assertEquals(500, body.status());
        assertNotNull(body.timestamp());

        // Internal exception details must not reach the client.
        assertEquals(false,
                body.message().contains("Database password leaked"));
    }
}
