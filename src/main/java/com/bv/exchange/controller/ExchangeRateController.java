package com.bv.exchange.controller;

import com.bv.exchange.model.ExchangeRateResponse;
import com.bv.exchange.model.ValueConversionResponse;
import com.bv.exchange.model.validation.ValidCurrencyCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
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
            summary =
                    "For a given source currency, get a target currency exchange rate (if specified) or all the currency exchange rate.",
            description =
                    "Returns the currency exchange rate of a target currency (if available) wrt given source currency or else all the other currency exchange rates are returned.",
            tags = {"exchange-rate"})
    @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(schema = @Schema(implementation = ExchangeRateResponse.class)))
    @ApiResponse(responseCode = "503", description = "External exchange rate server unavailable.")
    @ApiResponse(responseCode = "422", description = "Input validation error occurred.")
    @ApiResponse(responseCode = "500", description = "Internal Server error.")
    @GetMapping(
            value = {"from/{source}", "from/{source}/to/{target}"},
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ExchangeRateResponse> getExchangeRate(
            @ValidCurrencyCode @PathVariable String source,
            @ValidCurrencyCode @PathVariable(required = false) String target);

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
            @NotEmpty(message = "at least one currency must be specified!") @RequestParam
                    List<@ValidCurrencyCode String> currencies);
}
