package com.bv.exchange.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ExchangeRateResponse {
    private String sourceCurrency;
    private Map<String, BigDecimal> currencyExchangeRateMap;
}
