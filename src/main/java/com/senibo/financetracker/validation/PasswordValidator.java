package com.senibo.financetracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SYMBOL_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).matches();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).matches();
        boolean hasSymbol = SYMBOL_PATTERN.matcher(password).matches();

        if (!hasUppercase || !hasDigit || !hasSymbol) {
            context.disableDefaultConstraintViolation();

            StringBuilder message = new StringBuilder("Password must contain:");
            if (!hasUppercase) {
                message.append(" at least one uppercase letter");
            }
            if (!hasDigit) {
                if (!hasUppercase) message.append(",");
                message.append(" at least one number");
            }
            if (!hasSymbol) {
                if (!hasUppercase || !hasDigit) message.append(",");
                message.append(" at least one symbol");
            }

            context.buildConstraintViolationWithTemplate(message.toString())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
