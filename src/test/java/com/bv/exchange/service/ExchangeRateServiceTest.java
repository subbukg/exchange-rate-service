package com.bv.exchange.service;

import com.bv.exchange.model.ExchangeRateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {
    @Mock private ExchangeRateFetchService exchangeRateFetchService;
    @InjectMocks private ExchangeRateService exchangeRateService;

    @Test
    void testGetExchangeRate_ForASourceCurrency() {
        // arrange
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

        when(exchangeRateFetchService.getAllExchangeRates(sourceCurrency))
                .thenReturn(exchangeRateDto);
        // act
        final var exchangeRate = exchangeRateService.getExchangeRate(sourceCurrency, null);
        // assert
        assertThat(exchangeRate.getSourceCurrency()).isEqualTo(sourceCurrency);
        assertThat(exchangeRate.getCurrencyExchangeRateMap().get("INR"))
                .isEqualTo(BigDecimal.valueOf(79.80));
        verify(exchangeRateFetchService).getAllExchangeRates(eq(sourceCurrency));
        verifyNoMoreInteractions(exchangeRateFetchService);
    }

    @Test
    void testGetExchangeRate_ForSourceAndTargetCurrency() {
        // arrange
        final var sourceCurrency = "EUR";
        final var targetCurrency = "USD";

        when(exchangeRateFetchService.getExchangeRate(sourceCurrency, targetCurrency))
                .thenReturn(BigDecimal.valueOf(0.90));
        // act
        final var exchangeRate =
                exchangeRateService.getExchangeRate(sourceCurrency, targetCurrency);
        // assert
        assertThat(exchangeRate.getCurrencyExchangeRateMap())
                .containsEntry("USD", BigDecimal.valueOf(0.90));
        verify(exchangeRateFetchService).getExchangeRate(eq(sourceCurrency), eq(targetCurrency));
        verifyNoMoreInteractions(exchangeRateFetchService);
    }

    @Test
    void testGetExchangeRate_ForIdentical_SourceAndTargetCurrency() {
        // arrange
        final var sourceCurrency = "EUR";
        final var targetCurrency = "EUR";
        // act
        final var exchangeRate =
                exchangeRateService.getExchangeRate(sourceCurrency, targetCurrency);
        // assert
        assertThat(exchangeRate.getCurrencyExchangeRateMap()).containsEntry("EUR", BigDecimal.ONE);
        verifyNoInteractions(exchangeRateFetchService);
    }

    @Test
    void testGetValueConversionFor_ASingleTargetCurrency() {
        // arrange
        final var sourceCurrency = "EUR";
        final var value = BigDecimal.valueOf(2000);
        final var targetCurrency = "INR";
        when(exchangeRateFetchService.getExchangeRate(sourceCurrency, targetCurrency))
                .thenReturn(BigDecimal.valueOf(80.00));
        // act
        final var valueConversionResponse =
                exchangeRateService.getValueConversion(
                        sourceCurrency, value, singletonList(targetCurrency));
        // assert
        assertThat(valueConversionResponse.getCurrencyValueMap())
                .containsEntry("INR", BigDecimal.valueOf(160000.0));
        verify(exchangeRateFetchService).getExchangeRate(eq(sourceCurrency), eq(targetCurrency));
        verifyNoMoreInteractions(exchangeRateFetchService);
    }

    @Test
    void testGetValueConversionFor_AListOfTargetCurrencies() {
        // arrange
        final var sourceCurrency = "EUR";
        final var value = BigDecimal.valueOf(2000);
        final var targetCurrencyList = List.of("INR", "USD");

        when(exchangeRateFetchService.getAllExchangeRates(sourceCurrency))
                .thenReturn(
                        ExchangeRateDto.builder()
                                .rates(
                                        Map.of(
                                                "INR",
                                                BigDecimal.valueOf(80.00),
                                                "USD",
                                                BigDecimal.valueOf(0.90)))
                                .build());
        // act
        final var valueConversionResponse =
                exchangeRateService.getValueConversion(sourceCurrency, value, targetCurrencyList);
        // assert
        assertThat(valueConversionResponse.getCurrencyValueMap())
                .containsEntry("INR", BigDecimal.valueOf(160000.0))
                .containsEntry("USD", BigDecimal.valueOf(1800.0));
        verify(exchangeRateFetchService).getAllExchangeRates(eq(sourceCurrency));
        verifyNoMoreInteractions(exchangeRateFetchService);
    }
}
