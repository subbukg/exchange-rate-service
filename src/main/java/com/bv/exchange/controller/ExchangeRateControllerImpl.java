package com.bv.exchange.controller;

import com.bv.exchange.model.ExchangeRateResponse;
import com.bv.exchange.model.ValueConversionResponse;
import com.bv.exchange.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExchangeRateControllerImpl implements ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @Override
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(String source, String target) {
        return ResponseEntity.ok(exchangeRateService.getExchangeRate(source, target));
    }

    public ResponseEntity<ValueConversionResponse> getValueConversion(
            String source, BigDecimal value, List<String> targetCurrencies) {
        return ResponseEntity.ok(
                exchangeRateService.getValueConversion(source, value, targetCurrencies));
    }
}
