package com.bv.exchange.service;

import com.bv.exchange.model.ExchangeRateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ExchangeRateComputeService {
    private static final String EURO_BASE_CURRENCY = "EUR";
    private static final int SCALE = 2;
    private final ExternalExchangeRateServiceAdapterImpl exchangeRateServiceAdapter;

    public BigDecimal getExchangeRate(String source, String target) {
        if (source.equalsIgnoreCase(target)) {
            return BigDecimal.ONE;
        } else {
            final var exchangeRatesForEuro =
                    exchangeRateServiceAdapter.getLatestExchangeRates().getRates();

            if (EURO_BASE_CURRENCY.equalsIgnoreCase(source)) {
                return exchangeRatesForEuro.get(target).setScale(SCALE, RoundingMode.HALF_DOWN);
            } else {
                final var sourceExchangeForBase = exchangeRatesForEuro.get(source);
                final var targetExchangeForBase = exchangeRatesForEuro.get(target);
                return targetExchangeForBase.divide(
                        sourceExchangeForBase, RoundingMode.HALF_DOWN).setScale(SCALE, RoundingMode.HALF_DOWN);
            }
        }
    }

    public ExchangeRateDto getAllExchangeRates(String source) {
        return EURO_BASE_CURRENCY.equalsIgnoreCase(source)
                ? exchangeRateServiceAdapter.getLatestExchangeRates()
                : convertToSourceExchangeRate(source);
    }

    private ExchangeRateDto convertToSourceExchangeRate(String source) {
        Map<String, BigDecimal> exchangeRatesForSource = new HashMap<>();

        final var latestExchangeRatesForEuro =
                exchangeRateServiceAdapter.getLatestExchangeRates().getRates();
        final var sourceExchangeForEuro = latestExchangeRatesForEuro.get(source);

        latestExchangeRatesForEuro
                .keySet()
                .forEach(
                        currency -> {
                            final var currencyExchangeForEuro =
                                    latestExchangeRatesForEuro.get(currency);
                            exchangeRatesForSource.put(
                                    currency,
                                    currencyExchangeForEuro.divide(
                                            sourceExchangeForEuro, RoundingMode.HALF_DOWN).setScale(SCALE, RoundingMode.HALF_DOWN));
                        });
        return ExchangeRateDto.builder().base(source).rates(exchangeRatesForSource).build();
    }
}
