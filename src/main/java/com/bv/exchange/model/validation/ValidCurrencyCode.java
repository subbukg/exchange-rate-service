package com.bv.exchange.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidCurrencyCodeValidator.class)
public @interface ValidCurrencyCode {

    String message() default "Only valid standard currency codes are supported!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
