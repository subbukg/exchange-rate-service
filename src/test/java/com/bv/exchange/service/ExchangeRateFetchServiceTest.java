package com.bv.exchange.service;

import com.bv.exchange.model.ExchangeRateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateFetchServiceTest {
    @Mock private ExternalExchangeRateServiceAdapterImpl exchangeRateServiceAdapter;
    @InjectMocks private ExchangeRateFetchService exchangeRateFetchService;

    @Test
    void testGetExchangeRateForASourceAndTargetCurrencyWithEuroAsBaseCurrency() {
        // arrange
        final var sourceCurrency = "EUR";
        final var targetCurrency = "USD";

        when(exchangeRateServiceAdapter.getLatestExchangeRates())
                .thenReturn(
                        ExchangeRateDto.builder()
                                .base(sourceCurrency)
                                .rates(Map.of("USD", BigDecimal.valueOf(0.90)))
                                .build());
        // act
        final var exchangeRate =
                exchangeRateFetchService.getExchangeRate(sourceCurrency, targetCurrency);
        // assert
        assertThat(exchangeRate).isEqualTo(BigDecimal.valueOf(0.90));
        verify(exchangeRateServiceAdapter).getLatestExchangeRates();
    }

    @Test
    void testGetExchangeRateForASourceAndTargetCurrencyWithUSDAsBaseCurrency() {
        // arrange
        final var sourceCurrency = "USD";
        final var targetCurrency = "INR";

        when(exchangeRateServiceAdapter.getLatestExchangeRates())
                .thenReturn(
                        ExchangeRateDto.builder()
                                .base(sourceCurrency)
                                .rates(Map.of("INR", BigDecimal.valueOf(80.0), "USD", BigDecimal.valueOf(0.90)))
                                .build());
        // act
        final var exchangeRate =
                exchangeRateFetchService.getExchangeRate(sourceCurrency, targetCurrency);
        // assert
        assertThat(exchangeRate).isEqualTo(BigDecimal.valueOf(88.9));
        verify(exchangeRateServiceAdapter).getLatestExchangeRates();
    }

}
