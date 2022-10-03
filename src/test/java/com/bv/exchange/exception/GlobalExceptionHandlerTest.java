package com.bv.exchange.exception;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    private ServletWebRequest getWebRequest() {
        final var mockRequest = new MockHttpServletRequest("POST", "http://localhost/api/v1/mock");
        return new ServletWebRequest(mockRequest);
    }

    @Test
    void testHandleConstraintViolation() {
        final var violation =
                ConstraintViolationImpl.forParameterValidation(
                        "Parameter cannot be empty",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "",
                        PathImpl.createPathFromString("myClass.myProperty"),
                        null,
                        null,
                        null);
        final var exception = new ConstraintViolationException(Set.of(violation));

        final var res =
                globalExceptionHandler.handleConstraintViolation(exception, getWebRequest());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, res.getStatusCode());
        final var errorResponse = (ValidationErrorResponse) res.getBody();
        assertNotNull(errorResponse);
        assertEquals("myProperty", errorResponse.getErrors().get(0).getField());
    }

    @Test
    void testHandleAllUncaughtException() {
        final var exception = new RuntimeException("test");

        final var res =
                globalExceptionHandler.handleAllUncaughtException(exception, getWebRequest());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
        assertNotNull(res.getBody());
    }
}
