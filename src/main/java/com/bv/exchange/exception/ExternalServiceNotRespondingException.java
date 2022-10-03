package com.bv.exchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
public class ExternalServiceNotRespondingException extends RuntimeException {
    public ExternalServiceNotRespondingException(String message) {
        super(message);
    }
}
