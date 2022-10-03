package com.bv.exchange.model.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

@Component
public class ValidCurrencyCodeValidator implements ConstraintValidator<ValidCurrencyCode, String> {

    @Value("#{'${currency.codes}'.split(',')}")
    private Set<String> currencyCodes;

    @Override
    public boolean isValid(
            String currencyCode, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isEmpty(currencyCode)
                || (StringUtils.length(currencyCode) != 3 && currencyCodes.contains(currencyCode));
    }
}
