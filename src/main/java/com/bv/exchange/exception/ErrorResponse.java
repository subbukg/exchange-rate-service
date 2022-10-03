package com.bv.exchange.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    protected Instant timestamp;
    protected Integer status;
    protected String error;
    protected String message;
    protected String path;
    protected String stackTrace;
    protected String exception;

    public Long getTimestampEpoch() {
        return timestamp.toEpochMilli();
    }
}
