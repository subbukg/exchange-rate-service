package com.bv.exchange.service;

import com.bv.exchange.exception.ExternalServiceNotRespondingException;
import com.bv.exchange.model.ExchangeRateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ExternalExchangeRateServiceAdapterImpl implements ExternalExchangeRateServiceAdapter {
    private static final String EURO_BASE_CURRENCY = "EUR";
    private final RestTemplate restTemplate;

    @Cacheable("exchangeRate")
    @Override
    public ExchangeRateDto getLatestExchangeRates() throws ExternalServiceNotRespondingException {
        final var base = "base";
        final var sourceUrl = "https://api.exchangerate.host/latest";
        try {
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put(
                    base,
                    EURO_BASE_CURRENCY); // this part could be generalized to use diff base currency
            return restTemplate.getForObject(sourceUrl, ExchangeRateDto.class, uriVariables);

        } catch (Exception exception) {
            throw new ExternalServiceNotRespondingException(
                    String.format("The service: %s is currently unavailable.", sourceUrl));
        }
    }
}
