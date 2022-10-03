package com.bv.exchange.exception;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class ValidationError implements Serializable {
    private String field;
    private Serializable invalidValue;
    private String message;
}
