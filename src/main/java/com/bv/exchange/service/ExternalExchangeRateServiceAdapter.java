package com.bv.exchange.service;

import com.bv.exchange.exception.ExternalServiceNotRespondingException;
import com.bv.exchange.model.ExchangeRateDto;

public interface ExternalExchangeRateServiceAdapter {

    ExchangeRateDto getLatestExchangeRates() throws ExternalServiceNotRespondingException;
}
