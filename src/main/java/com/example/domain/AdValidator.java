package com.example.domain;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author volodymyr.tsukur
 */
public class AdValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Ad.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final Ad ad = (Ad) target;
        if (ad.getAmount() == null || ad.getAmount().intValue() <= 0) {
            errors.rejectValue("amount", "Ad.amount.invalid", "Amount must be positive");
        }
    }
}
