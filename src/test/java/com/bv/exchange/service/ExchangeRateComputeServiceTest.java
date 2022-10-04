package com.bv.exchange.service;

import com.bv.exchange.model.ExchangeRateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateComputeServiceTest {
    @Mock private ExternalExchangeRateServiceAdapterImpl exchangeRateServiceAdapter;
    @InjectMocks private ExchangeRateComputeService exchangeRateComputeService;

    @Test
    void testGetExchangeRate_ForASourceAndTargetCurrency_WithEuroAsBaseCurrency() {
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
                exchangeRateComputeService.getExchangeRate(sourceCurrency, targetCurrency);
        // assert
        assertThat(exchangeRate).isEqualTo(BigDecimal.valueOf(0.90).setScale(2, RoundingMode.HALF_UP));
        verify(exchangeRateServiceAdapter).getLatestExchangeRates();
    }

    @Test
    void testGetExchangeRate_ForASourceAndTargetCurrency_WithUSDAsBaseCurrency() {
        // arrange
        final var sourceCurrency = "USD";
        final var targetCurrency = "INR";

        when(exchangeRateServiceAdapter.getLatestExchangeRates())
                .thenReturn(
                        ExchangeRateDto.builder()
                                .base(sourceCurrency)
                                .rates(
                                        Map.of(
                                                "INR",
                                                BigDecimal.valueOf(8000,2),
                                                "USD",
                                                BigDecimal.valueOf(90, 2)))
                                .build());
        // act
        final var exchangeRate =
                exchangeRateComputeService.getExchangeRate(sourceCurrency, targetCurrency);
        // assert
        assertThat(exchangeRate).isEqualTo(BigDecimal.valueOf(8889, 2));
        verify(exchangeRateServiceAdapter).getLatestExchangeRates();
    }

    @Test
    void testGetExchangeRate_ForSameSourceAndTargetCurrency() {
        // arrange
        final var sourceCurrency = "USD";
        final var targetCurrency = "USD";

        // act
        final var exchangeRate =
                exchangeRateComputeService.getExchangeRate(sourceCurrency, targetCurrency);
        // assert
        assertThat(exchangeRate).isEqualTo(BigDecimal.ONE);
        verifyNoInteractions(exchangeRateServiceAdapter);
    }

    @Test
    void testGetAllExchangeRates_ForASource_WithEuroAsBaseCurrency() {
        // arrange
        final var sourceCurrency = "EUR";

        when(exchangeRateServiceAdapter.getLatestExchangeRates())
                .thenReturn(
                        ExchangeRateDto.builder()
                                .base(sourceCurrency)
                                .rates(
                                        Map.of(
                                                "INR",
                                                BigDecimal.valueOf(80),
                                                "USD",
                                                BigDecimal.valueOf(90, 2)))
                                .build());
        // act
        final var exchangeRate = exchangeRateComputeService.getAllExchangeRates(sourceCurrency);
        // assert
        assertThat(exchangeRate.getBase()).isEqualTo(sourceCurrency);
        assertThat(exchangeRate.getRates()).containsEntry("USD", BigDecimal.valueOf(90, 2));
        verify(exchangeRateServiceAdapter).getLatestExchangeRates();
    }

    @Test
    void testGetAllExchangeRates_ForASource_WithUSDasBaseCurrency() {
        // arrange
        final var sourceCurrency = "USD";

        when(exchangeRateServiceAdapter.getLatestExchangeRates())
                .thenReturn(
                        ExchangeRateDto.builder()
                                .base(sourceCurrency)
                                .rates(
                                        Map.of(
                                                "INR",
                                                BigDecimal.valueOf(80),
                                                "USD",
                                                BigDecimal.valueOf(90, 2)))
                                .build());
        // act
        final var exchangeRate = exchangeRateComputeService.getAllExchangeRates(sourceCurrency);
        // assert
        assertThat(exchangeRate.getBase()).isEqualTo(sourceCurrency);
        assertThat(exchangeRate.getRates())
                .containsEntry("INR", BigDecimal.valueOf(8900,2)); // converted value
        verify(exchangeRateServiceAdapter).getLatestExchangeRates();
    }
}
