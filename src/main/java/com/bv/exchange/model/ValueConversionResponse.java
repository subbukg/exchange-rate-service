package com.bv.exchange.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class ValueConversionResponse {
    private String sourceCurrency;
    private BigDecimal value;
    private Map<String, BigDecimal> currencyValueMap;
}
