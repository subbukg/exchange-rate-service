package com.bv.exchange.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    private final List<ValidationError> errors;

    @Builder(builderMethodName = "validationErrorBuilder")
    public ValidationErrorResponse(
            String message, String path, List<ValidationError> errors, String exception) {
        super(
                Instant.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                message,
                path,
                null,
                exception);
        this.errors = CollectionUtils.isEmpty(errors) ? null : new ArrayList<>(errors);
    }
}
