package com.bv.exchange.service;

import com.bv.exchange.model.ExchangeRateDto;
import com.bv.exchange.model.ExchangeRateResponse;
import com.bv.exchange.model.ValueConversionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeRateService {

    private final ExchangeDataFetchService exchangeDataFetchService;

    public ExchangeRateResponse getExchangeRate(String sourceCurrency, String targetCurrency) {
        Map<String, BigDecimal> currencyExchangeRateMap = new HashMap<>();
        if (StringUtils.isBlank(targetCurrency)) {
            currencyExchangeRateMap =
                    exchangeDataFetchService.getAllExchangeRates(sourceCurrency).getRates();
        } else {
            BigDecimal exchangeRate =
                    sourceCurrency.equalsIgnoreCase(targetCurrency)
                            ? BigDecimal.ONE
                            : exchangeDataFetchService.getExchangeRate(
                                    sourceCurrency, targetCurrency);

            currencyExchangeRateMap.put(targetCurrency, exchangeRate);
        }

        return ExchangeRateResponse.builder()
                .sourceCurrency(sourceCurrency)
                .currencyExchangeRateMap(currencyExchangeRateMap)
                .build();
    }

    public ValueConversionResponse getValueConversion(
            String sourceCurrency, BigDecimal value, List<String> targetCurrencies) {
        Map<String, BigDecimal> currencyValueMap = new HashMap<>();

        if (targetCurrencies.size() == 1) {
            final var exchangeRate =
                    exchangeDataFetchService.getExchangeRate(
                            sourceCurrency, targetCurrencies.get(0));
            currencyValueMap.put(targetCurrencies.get(0), value.multiply(exchangeRate));
        } else {
            final var sourceExchangeRate =
                    exchangeDataFetchService.getAllExchangeRates(sourceCurrency);
            computeCurrencyValues(value, targetCurrencies, currencyValueMap, sourceExchangeRate);
        }

        return ValueConversionResponse.builder()
                .sourceCurrency(sourceCurrency)
                .value(value)
                .currencyValueMap(currencyValueMap)
                .build();
    }

    private void computeCurrencyValues(
            BigDecimal value,
            List<String> targetCurrencies,
            Map<String, BigDecimal> currencyValueMap,
            ExchangeRateDto sourceExchangeRate) {

        if (!sourceExchangeRate.getRates().isEmpty()) {
            sourceExchangeRate
                    .getRates()
                    .forEach(
                            (s, rate) -> {
                                if (targetCurrencies.contains(s)) {
                                    currencyValueMap.put(s, value.multiply(rate));
                                }
                            });
        }
    }
}
