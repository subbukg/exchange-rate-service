package com.bv.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ExchangeRateDto implements Serializable {

    private String base;
    private Map<String, BigDecimal> rates;
}
