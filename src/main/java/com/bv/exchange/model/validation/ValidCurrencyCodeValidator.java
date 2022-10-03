package com.bv.exchange.model.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ValidCurrencyCodeValidator implements ConstraintValidator<ValidCurrencyCode, String> {

    @Value("#{'${currency.codes}'.split(',')}")
    private final Set<String> currencyCodes;

    @Override
    public boolean isValid(
            String currencyCode, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isNotEmpty(currencyCode)) {
            return currencyCodes.contains(currencyCode);
        }
        return true;
    }
}
