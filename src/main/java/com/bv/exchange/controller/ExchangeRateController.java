package com.bv.exchange.controller;

import com.bv.exchange.model.ExchangeRateResponse;
import com.bv.exchange.model.ValueConversionResponse;
import com.bv.exchange.model.validation.ValidCurrencyCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@Validated
@RequestMapping(path = "/api/v1/currency-exchange/")
public interface ExchangeRateController {

    @Operation(
            summary = "For a given source currency, get the target currency exchange rate.",
            description = "Returns the currency exchange rate of a target currency.",
            tags = {"exchange-rate"})
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ExchangeRateResponse.class)))
    @ApiResponse(responseCode = "503", description = "External exchange rate server unavailable.")
    @ApiResponse(responseCode = "422", description = "Input validation error occurred.")
    @ApiResponse(responseCode = "500", description = "Internal Server error.")
    @GetMapping(value = "from/{source}/to/{target}")
    ResponseEntity<ExchangeRateResponse> getExchangeRateForTargetCurrency(
            @ValidCurrencyCode @PathVariable String source,
            @ValidCurrencyCode @PathVariable String target);

    @Operation(
            summary = "For a given source currency, get all the currency exchange rates.",
            description =
                    "For a source currency, returns the exchange rates of all the other currencies.",
            tags = {"exchange-rate"})
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ExchangeRateResponse.class)))
    @ApiResponse(responseCode = "503", description = "External exchange rate server unavailable.")
    @ApiResponse(responseCode = "422", description = "Input validation error occurred.")
    @ApiResponse(responseCode = "500", description = "Internal Server error.")
    @GetMapping(value = "from/{source}")
    ResponseEntity<ExchangeRateResponse> getExchangeRates(
            @ValidCurrencyCode @PathVariable String source);

    @Operation(
            summary = "Get value conversion from source currency to target currency(ies).",
            description =
                    "For a given source currency and value, convert it into target currency(ies) value.",
            tags = {"value-conversion"})
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ValueConversionResponse.class)))
    @ApiResponse(responseCode = "503", description = "External exchange rate server unavailable.")
    @ApiResponse(responseCode = "422", description = "Input validation error occurred.")
    @ApiResponse(responseCode = "500", description = "Internal Server error.")
    @GetMapping("from/{source}/value/{value}")
    ResponseEntity<ValueConversionResponse> getValueConversion(
            @ValidCurrencyCode @PathVariable String source,
            @DecimalMin(value = "0.0", inclusive = false) @PathVariable BigDecimal value,
            @Parameter(
                            in = ParameterIn.QUERY,
                            description = "The target currency for the value conversion.")
                    @NotEmpty(message = "at least one currency must be specified!")
                    @RequestParam
                    List<@ValidCurrencyCode String> currencies);
}
