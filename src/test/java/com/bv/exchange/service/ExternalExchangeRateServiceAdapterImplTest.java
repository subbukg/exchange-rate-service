package com.bv.exchange.service;

import com.bv.exchange.exception.ExternalServiceNotRespondingException;
import com.bv.exchange.model.ExchangeRateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalExchangeRateServiceAdapterImplTest {
    @Mock private RestTemplate restTemplate;
    @InjectMocks private ExternalExchangeRateServiceAdapterImpl exchangeRateServiceAdapter;

    @Test
    void testGetLatestExchangeRate_ReturnsMockedObject() {
        // arrange
        final var sourceUrl = "https://api.exchangerate.host/latest";
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("base", "EUR");
        final var sourceCurrency = "EUR";
        final var exchangeRateDto =
                ExchangeRateDto.builder()
                        .base("EUR")
                        .rates(
                                Map.of(
                                        "INR",
                                        BigDecimal.valueOf(79.80),
                                        "USD",
                                        BigDecimal.valueOf(80.25)))
                        .build();

        when(restTemplate.getForObject(sourceUrl, ExchangeRateDto.class, uriVariables))
                .thenReturn(exchangeRateDto);
        // act
        final var exchangeRate = exchangeRateServiceAdapter.getLatestExchangeRates();
        // assert
        assertThat(exchangeRate.getBase()).isEqualTo(sourceCurrency);
        assertThat(exchangeRate.getRates()).containsEntry("INR", BigDecimal.valueOf(79.80));
        verify(restTemplate)
                .getForObject(eq(sourceUrl), eq(ExchangeRateDto.class), eq(uriVariables));
    }

    @Test
    void testGetLatestExchange_ThrowsException_DueToServiceUnavailability() {
        // arrange
        final var sourceUrl = "https://api.exchangerate.host/latest";
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("base", "EUR");
        when(restTemplate.getForObject(sourceUrl, ExchangeRateDto.class, uriVariables))
                .thenThrow(
                        new ExternalServiceNotRespondingException(
                                String.format(
                                        "The service: %s is currently unavailable.", sourceUrl)));
        // act & assert
        assertThrows(
                ExternalServiceNotRespondingException.class,
                () -> exchangeRateServiceAdapter.getLatestExchangeRates());
    }
}
