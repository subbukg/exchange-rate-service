package com.bv.exchange.controller;

import com.bv.exchange.ExchangeRateServiceApplication;
import com.bv.exchange.model.ExchangeRateResponse;
import com.bv.exchange.model.ValueConversionResponse;
import com.bv.exchange.service.ExchangeRateService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = {ExchangeRateServiceApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExchangeRateControllerTest {

    @MockBean private ExchangeRateService exchangeRateService;

    @BeforeAll
    static void setUp(@Value("${local.server.port}") int port) {
        RestAssured.port = port;
    }

    @Test
    void getExchangeRate_WhenValidSourceIsGiven() {
        final var source = "EUR";
        final var exchangeRateResponse =
                ExchangeRateResponse.builder()
                        .sourceCurrency(source)
                        .currencyExchangeRateMap(Map.of())
                        .build();

        when(exchangeRateService.getExchangeRate(anyString(), any(String.class)))
                .thenReturn(exchangeRateResponse);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/currency-exchange/from/" + source)
                .then()
                .statusCode(200);
        verify(exchangeRateService).getExchangeRate(eq(source), eq(null));
    }

    @Test
    void getExchangeRate_WhenValidSourceAndTargetIsGiven() {
        final var source = "EUR";
        final var target = "USD";
        final var exchangeRateResponse =
                ExchangeRateResponse.builder()
                        .sourceCurrency(source)
                        .currencyExchangeRateMap(Map.of())
                        .build();
        when(exchangeRateService.getExchangeRate(anyString(), any(String.class)))
                .thenReturn(exchangeRateResponse);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/currency-exchange/from/" + source + "/to/" + target)
                .then()
                .statusCode(200);
        verify(exchangeRateService).getExchangeRate(eq(source), eq(target));
    }

    @Test
    void getExchangeRate_WhenInvalidSourceCurrencyIsGiven() {
        final var source = "XYZ"; // invalid currency
        final var exchangeRateResponse =
                ExchangeRateResponse.builder()
                        .sourceCurrency(source)
                        .currencyExchangeRateMap(Map.of())
                        .build();
        when(exchangeRateService.getExchangeRate(anyString(), any(String.class)))
                .thenReturn(exchangeRateResponse);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/currency-exchange/from/" + source)
                .then()
                .statusCode(422);
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void getValueConversion_WhenValidSourceCurrencyIsGiven() {
        final var source = "EUR";
        final var target = "INR";
        final var currencies = List.of(target);
        final var value = BigDecimal.valueOf(2000);
        final var exchangeRateResponse =
                ValueConversionResponse.builder()
                        .sourceCurrency(source)
                        .value(value)
                        .currencyValueMap(Map.of())
                        .build();
        when(exchangeRateService.getValueConversion(anyString(), any(), any()))
                .thenReturn(exchangeRateResponse);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .queryParam("currencies", List.of("INR"))
                .get("/api/v1/currency-exchange/from/" + source + "/value/" + value)
                .then()
                .statusCode(200);
        verify(exchangeRateService).getValueConversion(eq(source), eq(value), eq(currencies));
    }
}
