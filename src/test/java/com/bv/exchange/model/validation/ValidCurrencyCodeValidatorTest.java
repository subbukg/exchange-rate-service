package com.bv.exchange.model.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import java.util.Set;

import static org.mockito.Mockito.mock;

class ValidCurrencyCodeValidatorTest {

    private final ConstraintValidatorContext constraintValidatorContext =
            mock(ConstraintValidatorContext.class);

    @Test
    void testValidateCurrencyCodeWithNullCurrencyCode() {
        ValidCurrencyCodeValidator validator = new ValidCurrencyCodeValidator(Set.of("USD"));
        // assert true because, this field wasn't set so no need to validate
        Assertions.assertTrue(validator.isValid(null, constraintValidatorContext));
    }

    @Test
    void testValidateCurrencyCodeWithUSDCurrencyCode() {
        ValidCurrencyCodeValidator validator = new ValidCurrencyCodeValidator(Set.of("USD"));
        // assert true because, this currency code exists
        Assertions.assertTrue(validator.isValid("USD", constraintValidatorContext));
    }

    @Test
    void testValidateCurrencyCodeWithInvalidCurrencyCode() {
        ValidCurrencyCodeValidator validator =
                new ValidCurrencyCodeValidator(Set.of("USD", "EUR", "INR"));
        // assert false because, the currency code specified is invalid
        Assertions.assertFalse(validator.isValid("XYZ", constraintValidatorContext));
    }
}
