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
public class ExchangeDataFetchService {
    private static final String EURO_BASE_CURRENCY = "EUR";
    private final ExternalExchangeRateServiceAdapterImpl exchangeRateService;


    public BigDecimal getExchangeRate(String source, String target) {
        if (source.equalsIgnoreCase(target)) {
            return BigDecimal.ONE;
        } else {
            final var exchangeRatesForEuro =
                    exchangeRateService.getLatestExchangeRates().getRates();

            if (EURO_BASE_CURRENCY.equalsIgnoreCase(source)) {
                return exchangeRatesForEuro.get(target);
            } else {
                final var sourceExchangeForBase = exchangeRatesForEuro.get(source);
                final var targetExchangeForBase = exchangeRatesForEuro.get(target);
                return targetExchangeForBase.divide(sourceExchangeForBase, RoundingMode.HALF_DOWN);
            }
        }
    }

    public ExchangeRateDto getAllExchangeRates(String source) {
        return EURO_BASE_CURRENCY.equalsIgnoreCase(source)
                ? exchangeRateService.getLatestExchangeRates()
                : convertToSourceExchangeRate(source);
    }

    private ExchangeRateDto convertToSourceExchangeRate(String source) {
        Map<String, BigDecimal> exchangeRatesForSource = new HashMap<>();

        final var latestExchangeRatesForEuro =
                exchangeRateService.getLatestExchangeRates().getRates();
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
                                            sourceExchangeForEuro, RoundingMode.HALF_DOWN));
                        });
        return ExchangeRateDto.builder().base(source).rates(exchangeRatesForSource).build();
    }

}
