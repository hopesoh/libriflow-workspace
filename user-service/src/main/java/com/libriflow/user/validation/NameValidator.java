package com.libriflow.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NameValidator implements ConstraintValidator<ValidName, String> {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L}\\p{M} '-]{1,119}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String normalized = value.trim();
        return !normalized.isBlank() && NAME_PATTERN.matcher(normalized).matches();
    }
}
