package com.bv.exchange.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String INPUT_VALIDATION_FAILED = "Input validation failed.";

    @ExceptionHandler(ExternalServiceNotRespondingException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponse> handleExternalServiceUnavailable(
            ExternalServiceNotRespondingException ex, WebRequest request) {

        log.debug("External Rate Exchange Service exception encountered: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                        .message(ex.getMessage())
                        .exception(ex.getClass().getName())
                        .path(((ServletWebRequest) request).getRequest().getRequestURI())
                        .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        log.debug(
                "Constraint violation exception encountered: {}", ex.getConstraintViolations(), ex);
        List<ValidationError> errors = buildValidationErrors(ex.getConstraintViolations());

        ValidationErrorResponse validationErrorResponse =
                ValidationErrorResponse.validationErrorBuilder()
                        .message(INPUT_VALIDATION_FAILED)
                        .exception(ex.getClass().getName())
                        .path(((ServletWebRequest) request).getRequest().getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.unprocessableEntity().body(validationErrorResponse);
    }

    /** Build list of ValidationError from set of ConstraintViolation */
    private List<ValidationError> buildValidationErrors(Set<ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(
                        violation ->
                                ValidationError.builder()
                                        .field(
                                                Objects.requireNonNull(StreamSupport.stream(
                                                                        violation
                                                                                .getPropertyPath()
                                                                                .spliterator(),
                                                                        false)
                                                                .reduce((first, second) -> second)
                                                                .orElse(null))
                                                        .toString())
                                        .message(violation.getMessage())
                                        .invalidValue((Serializable) violation.getInvalidValue())
                                        .build())
                .collect(toList());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
            RuntimeException exception, WebRequest request) {
        log.error("Unknown error occurred", exception);
        ErrorResponse response =
                ErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .message(exception.getMessage())
                        .path(((ServletWebRequest) request).getRequest().getRequestURI())
                        .exception(exception.getClass().getName())
                        .build();
        if (exception.getStackTrace() != null) {
            try (StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter)) {
                exception.printStackTrace(printWriter);
                response.setStackTrace(stringWriter.toString());
            } catch (IOException ex) {
                log.error("StringWriter exception", ex);
            }
        }
        return ResponseEntity.internalServerError().body(response);
    }
}
